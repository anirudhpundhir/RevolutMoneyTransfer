package currency;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;

/**
 * Deterministic, offline exchange-rate provider. This is the default so the
 * service runs and tests pass without network access or an API key.
 *
 * <p>Rates are illustrative only, expressed as units per 1 EUR, and cross rates
 * are derived from them.
 */
public final class FixedRateProvider implements ExchangeRateProvider {

    private static final MathContext PRECISION = MathContext.DECIMAL64;

    private static final Map<String, BigDecimal> DEFAULT_RATES_PER_EUR = Map.ofEntries(
            Map.entry("EUR", new BigDecimal("1")),
            Map.entry("USD", new BigDecimal("1.08")),
            Map.entry("GBP", new BigDecimal("0.85")),
            Map.entry("CHF", new BigDecimal("0.95")),
            Map.entry("INR", new BigDecimal("90")),
            Map.entry("RUB", new BigDecimal("100")),
            Map.entry("JPY", new BigDecimal("160")),
            Map.entry("SEK", new BigDecimal("11.5")),
            Map.entry("PLN", new BigDecimal("4.3"))
    );

    private final Map<String, BigDecimal> ratesPerEur;

    public FixedRateProvider() {
        this(DEFAULT_RATES_PER_EUR);
    }

    public FixedRateProvider(Map<String, BigDecimal> ratesPerEur) {
        this.ratesPerEur = Map.copyOf(ratesPerEur);
    }

    @Override
    public BigDecimal rate(String from, String to) {
        if (from.equals(to)) {
            return BigDecimal.ONE;
        }
        return quote(to).divide(quote(from), PRECISION);
    }

    @Override
    public boolean supports(String currency) {
        return currency != null && ratesPerEur.containsKey(currency);
    }

    private BigDecimal quote(String currency) {
        BigDecimal rate = ratesPerEur.get(currency);
        if (rate == null) {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
        return rate;
    }
}
