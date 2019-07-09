package entity;

import currency.CurrencyConvertor;

import javax.money.MonetaryAmount;
import java.util.Objects;

public class Money {


    private double amount;


    private String currency;

    public Money(double amount, String currency) {
        if (amount < 0)
            throw new IllegalArgumentException("Negative amount of money");
        this.amount = amount;
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Negative amount of money");
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    MonetaryAmount toMonetaryAmount() {
        return org.javamoney.moneta.Money.of(amount, currency);
    }

    public static Money fromMonetaryAmount(MonetaryAmount monetaryAmount) {
        return new Money(monetaryAmount.getNumber().doubleValueExact(), monetaryAmount.getCurrency().getCurrencyCode());
    }

    public static Money convert(Money money, String newCurrency) {
        if (newCurrency.equals(money.getCurrency()))
            return money;
        double conversionRate = CurrencyConvertor.getConversionRate(money.getCurrency(), newCurrency);
        return new Money(money.getAmount() * conversionRate, newCurrency);
    }

    public Money add(Money money) {
        double conversionRate = CurrencyConvertor.getConversionRate(money.getCurrency(), this.currency);
        return new Money(amount + money.getAmount() * conversionRate, currency);
    }

    public Money sub(Money money) {
        double conversionRate = CurrencyConvertor.getConversionRate(money.getCurrency(), this.currency);
        return new Money(amount - money.getAmount() * conversionRate, currency);
    }

    public boolean isGreaterThan(Money money) {
        return amount > convert(money, currency).getAmount();
    }

    public boolean isLesserThan(Money money) {
        return amount < convert(money, currency).getAmount();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money that = (Money) o;
        return Double.compare(that.amount, amount) == 0 &&
                Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {

        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return "Money{" +
                "amount=" + amount +
                ", currency='" + currency + '\'' +
                '}';
    }

    public enum Currencies {

        AUD, BGN, BRL, CAD, CHF, CNY, CZK, DKK, GBP, HKD, HRK, EUR,
        HUF, IDR, ILS, INR, ISK, JPY, KRW, MXN, MYR, NOK, NZD, PHP, PLN, RON, RUB, SEK, SGD, THB, TRY, USD, ZAR;

        public static boolean contains(String test) {

            for (Currencies c : Currencies.values()) {
                if (c.name().equals(test)) {
                    return true;
                }
            }

            return false;
        }
    }

}
