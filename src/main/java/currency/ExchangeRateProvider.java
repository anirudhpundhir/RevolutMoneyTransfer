package currency;

import java.math.BigDecimal;

/**
 * Source of exchange rates. Implementations must be thread-safe.
 */
public interface ExchangeRateProvider {

    /** Returns how many units of {@code to} one unit of {@code from} buys. */
    BigDecimal rate(String from, String to);

    /** Whether this provider can quote the given ISO-4217 currency code. */
    boolean supports(String currency);
}
