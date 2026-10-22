package entity;

import currency.ExchangeRateProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Immutable, non-negative monetary amount.
 *
 * <p>Amounts are held as {@link BigDecimal} scaled to the currency's minor units
 * (e.g. 2 for EUR, 0 for JPY), so arithmetic is exact and never suffers from
 * binary floating point drift.
 */
public final class Money implements Comparable<Money> {

    private final BigDecimal amount;
    private final String currency;

    private Money(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    /** Creates money from user input; rejects negative values or more decimals than the currency allows. */
    public static Money of(BigDecimal amount, String currency) {
        Objects.requireNonNull(amount, "amount");
        String code = normalize(currency);
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Amount must not be negative");
        }
        try {
            return new Money(amount.setScale(scaleOf(code), RoundingMode.UNNECESSARY), code);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(
                    "Amount has more decimal places than " + code + " allows: " + amount.toPlainString());
        }
    }

    public static Money of(String amount, String currency) {
        if (amount == null || amount.isBlank()) {
            throw new IllegalArgumentException("Amount is required");
        }
        try {
            return of(new BigDecimal(amount.trim()), currency);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Amount is not a number: " + amount);
        }
    }

    public static Money of(long amount, String currency) {
        return of(BigDecimal.valueOf(amount), currency);
    }

    public static Money zero(String currency) {
        return of(BigDecimal.ZERO, currency);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public boolean isZero() {
        return amount.signum() == 0;
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    /** Subtracts {@code other}; throws if the result would be negative. */
    public Money subtract(Money other) {
        requireSameCurrency(other);
        BigDecimal result = amount.subtract(other.amount);
        if (result.signum() < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        return new Money(result, currency);
    }

    public boolean isLessThan(Money other) {
        return compareTo(other) < 0;
    }

    /** Converts to {@code targetCurrency}, rounding half-even to the target's minor units. */
    public Money convertTo(String targetCurrency, ExchangeRateProvider rates) {
        String target = normalize(targetCurrency);
        if (target.equals(currency)) {
            return this;
        }
        BigDecimal rate = rates.rate(currency, target);
        return new Money(amount.multiply(rate).setScale(scaleOf(target), RoundingMode.HALF_EVEN), target);
    }

    @Override
    public int compareTo(Money other) {
        requireSameCurrency(other);
        return amount.compareTo(other.amount);
    }

    private void requireSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch: " + currency + " vs " + other.currency);
        }
    }

    private static String normalize(String currency) {
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
        String code = currency.trim().toUpperCase(java.util.Locale.ROOT);
        try {
            Currency.getInstance(code);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown currency: " + currency);
        }
        return code;
    }

    private static int scaleOf(String code) {
        return Math.max(0, Currency.getInstance(code).getDefaultFractionDigits());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money that = (Money) o;
        return amount.equals(that.amount) && currency.equals(that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency;
    }
}
