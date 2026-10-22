package currency;

import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Live rates from exchangeratesapi.io, which requires an access key. Opt-in via
 * the {@code EXCHANGE_RATES_API_KEY} environment variable. Responses are cached
 * for {@code ttl} so the API is not called on every transfer.
 */
public final class HttpExchangeRateProvider implements ExchangeRateProvider {

    private static final String DEFAULT_BASE_URL = "https://api.exchangeratesapi.io/v1";
    private static final MathContext PRECISION = MathContext.DECIMAL64;

    private final HttpClient client;
    private final String baseUrl;
    private final String apiKey;
    private final Duration ttl;
    private final Clock clock;
    private final Gson gson = new Gson();

    private volatile Snapshot snapshot;

    public HttpExchangeRateProvider(String apiKey) {
        this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build(),
                DEFAULT_BASE_URL, apiKey, Duration.ofHours(1), Clock.systemUTC());
    }

    HttpExchangeRateProvider(HttpClient client, String baseUrl, String apiKey, Duration ttl, Clock clock) {
        this.client = Objects.requireNonNull(client);
        this.baseUrl = Objects.requireNonNull(baseUrl);
        this.apiKey = Objects.requireNonNull(apiKey);
        this.ttl = Objects.requireNonNull(ttl);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public BigDecimal rate(String from, String to) {
        if (from.equals(to)) {
            return BigDecimal.ONE;
        }
        Map<String, BigDecimal> rates = current().rates();
        BigDecimal fromRate = rates.get(from);
        BigDecimal toRate = rates.get(to);
        if (fromRate == null || toRate == null) {
            throw new IllegalArgumentException("Unsupported currency pair: " + from + "/" + to);
        }
        return toRate.divide(fromRate, PRECISION);
    }

    @Override
    public boolean supports(String currency) {
        return currency != null && current().rates().containsKey(currency);
    }

    private Snapshot current() {
        Snapshot s = snapshot;
        if (s == null || isStale(s)) {
            synchronized (this) {
                s = snapshot;
                if (s == null || isStale(s)) {
                    s = new Snapshot(fetch(), clock.instant());
                    snapshot = s;
                }
            }
        }
        return s;
    }

    private boolean isStale(Snapshot s) {
        return s.fetchedAt().plus(ttl).isBefore(clock.instant());
    }

    private Map<String, BigDecimal> fetch() {
        URI uri = URI.create(baseUrl + "/latest?access_key="
                + URLEncoder.encode(apiKey, StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(10)).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Exchange-rate API returned HTTP " + response.statusCode());
            }
            return parse(response.body());
        } catch (IOException e) {
            throw new IllegalStateException("Exchange-rate API unavailable", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while fetching exchange rates", e);
        }
    }

    Map<String, BigDecimal> parse(String body) {
        RatesResponse parsed = gson.fromJson(body, RatesResponse.class);
        if (parsed == null || !parsed.success || parsed.rates == null || parsed.base == null) {
            throw new IllegalStateException("Unexpected exchange-rate API response");
        }
        Map<String, BigDecimal> rates = new HashMap<>(parsed.rates);
        rates.put(parsed.base, BigDecimal.ONE);
        return Map.copyOf(rates);
    }

    private static final class RatesResponse {
        boolean success;
        String base;
        Map<String, BigDecimal> rates;
    }

    private record Snapshot(Map<String, BigDecimal> rates, Instant fetchedAt) {
    }
}
