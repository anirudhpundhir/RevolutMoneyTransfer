package entity;

import java.util.ArrayList;
import java.util.List;

import constants.ExceptionMessages;

public class Account {

    private String name;
    private String accountNumber;
    private Money fundsAvailable;
    private List<Transfer> transfers;
    private String currency;

    private static int accountNumberSequence = 1000000;

    public Account(String name) {
        this(name, "INR");
    }

    public Account(String name, String currency) {
        this(name, 1000, currency);

    }

    public Account(String name, double amount, String currency) {
        if (amount < 0)
            throw new IllegalArgumentException(ExceptionMessages.AMOUNT_LESSER_THAN_ZERO);
        if (name.isEmpty()) {
            throw new IllegalArgumentException(ExceptionMessages.NAME_EMPTY);
        }
        this.accountNumber = generateAccountNumber();
        this.name = name;
        this.currency = currency;
        this.fundsAvailable = new Money(amount, currency);
        this.transfers = new ArrayList<>();
    }


    public String getAccountNumber() {
        return accountNumber;
    }


    public List<Transfer> getTransfers() {
        return transfers;
    }

    public void setTransfers(List<Transfer> transfers) {
        this.transfers = transfers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    private String generateAccountNumber() {
        return String.valueOf(++accountNumberSequence);
    }

    public Money getFundsAvailable() {
        return fundsAvailable;
    }

    public void setFundsAvailable(Money fundsAvailable) {
        if (!currency.equals(fundsAvailable.getCurrency())) {
            throw new IllegalArgumentException(ExceptionMessages.WRONG_CURRENCY);
        }
        this.fundsAvailable = fundsAvailable;
    }

    public void setFundsAvailable(double fundsAvailable) {
        this.fundsAvailable.setAmount(fundsAvailable);
    }

    public void setCurrency(String currency) {
        this.currency = currency;
        this.setFundsAvailable(Money.convert(fundsAvailable, currency));
    }

    public void withdrawMoney(Money amount) {
        fundsAvailable = fundsAvailable.sub(amount);
    }

    public void addMoney(Money amount) {
        fundsAvailable = fundsAvailable.add(amount);
    }

}
