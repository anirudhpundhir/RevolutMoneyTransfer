package currency;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.Clock;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExchangeRateProviderTest {

    @Test
    void fixedProviderDerivesCrossRates() {
        FixedRateProvider rates = new FixedRateProvider();
        assertEquals(0, new BigDecimal("1.08").compareTo(rates.rate("EUR", "USD")));
        assertEquals(0, new BigDecimal("0.9").compareTo(rates.rate("RUB", "INR")));
        assertEquals(BigDecimal.ONE, rates.rate("GBP", "GBP"));
        assertTrue(rates.supports("INR"));
        assertFalse(rates.supports("XYZ"));
        assertThrows(IllegalArgumentException.class, () -> rates.rate("EUR", "XYZ"));
    }

    @Test
    void httpProviderParsesApiResponseAndAddsBase() {
        HttpExchangeRateProvider provider = new HttpExchangeRateProvider(
                HttpClient.newHttpClient(), "http://unused", "key", Duration.ofHours(1), Clock.systemUTC());
        Map<String, BigDecimal> rates = provider.parse(
                "{\"success\":true,\"base\":\"EUR\",\"rates\":{\"USD\":1.1,\"GBP\":0.8}}");
        assertEquals(BigDecimal.ONE, rates.get("EUR"));
        assertEquals(new BigDecimal("1.1"), rates.get("USD"));
        assertThrows(IllegalStateException.class,
                () -> provider.parse("{\"success\":false,\"error\":{\"code\":101}}"));
    }
}
