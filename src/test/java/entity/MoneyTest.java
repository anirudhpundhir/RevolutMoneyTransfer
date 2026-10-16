package entity;

import currency.FixedRateProvider;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoneyTest {

    @Test
    void scalesToCurrencyMinorUnits() {
        assertEquals(new BigDecimal("10.50"), Money.of("10.5", "EUR").getAmount());
        assertEquals(new BigDecimal("1000"), Money.of("1000", "JPY").getAmount());
    }

    @Test
    void arithmeticIsExact() {
        Money sum = Money.zero("USD");
        for (int i = 0; i < 10; i++) {
            sum = sum.add(Money.of("0.10", "USD"));
        }
        assertEquals(Money.of("1.00", "USD"), sum);
    }

    @Test
    void rejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> Money.of("-1", "EUR"));
        assertThrows(IllegalArgumentException.class, () -> Money.of("1.001", "EUR"));
        assertThrows(IllegalArgumentException.class, () -> Money.of("abc", "EUR"));
        assertThrows(IllegalArgumentException.class, () -> Money.of("1", "XXXX"));
        assertThrows(IllegalArgumentException.class, () -> Money.of("1", null));
    }

    @Test
    void subtractNeverGoesNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> Money.of("5", "EUR").subtract(Money.of("5.01", "EUR")));
    }

    @Test
    void mixedCurrencyArithmeticIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> Money.of("5", "EUR").add(Money.of("5", "USD")));
    }

    @Test
    void convertsWithHalfEvenRounding() {
        FixedRateProvider rates = new FixedRateProvider(Map.of(
                "EUR", BigDecimal.ONE, "USD", new BigDecimal("1.125")));
        assertEquals(Money.of("11.25", "USD"), Money.of("10", "EUR").convertTo("USD", rates));
        // 0.01 * 1.125 = 0.01125 -> 0.01
        assertEquals(Money.of("0.01", "USD"), Money.of("0.01", "EUR").convertTo("USD", rates));
        assertTrue(Money.of("1", "EUR").convertTo("eur", rates).equals(Money.of("1", "EUR")));
    }
}
