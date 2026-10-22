package service.impl;

import constants.ExceptionMessages;
import currency.ExchangeRateProvider;
import entity.Account;
import entity.Money;
import exception.ConflictException;
import exception.NotFoundException;
import service.AccountService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class AccountServiceMapImpl implements AccountService {

    private final Map<String, Account> storage = new ConcurrentHashMap<>();
    private final AtomicLong accountNumberSequence = new AtomicLong(1_000_000);
    private final ExchangeRateProvider rates;

    public AccountServiceMapImpl(ExchangeRateProvider rates) {
        this.rates = rates;
    }

    @Override
    public Account createAccount(String name, Money initialFunds) {
        if (!rates.supports(initialFunds.getCurrency())) {
            throw new IllegalArgumentException(ExceptionMessages.UNSUPPORTED_CURRENCY + ": " + initialFunds.getCurrency());
        }
        String number = String.valueOf(accountNumberSequence.incrementAndGet());
        Account account = new Account(number, name, initialFunds);
        storage.put(number, account);
        return account;
    }

    @Override
    public Collection<Account> getAccounts() {
        return List.copyOf(storage.values());
    }

    @Override
    public Account getAccount(String accountNumber) {
        Account account = accountNumber == null ? null : storage.get(accountNumber);
        if (account == null) {
            throw new NotFoundException(ExceptionMessages.ACCOUNT_NOT_FOUND + ": " + accountNumber);
        }
        return account;
    }

    @Override
    public Account renameAccount(String accountNumber, String newName) {
        Account account = getAccount(accountNumber);
        account.setName(newName);
        return account;
    }

    @Override
    public void deleteAccount(String accountNumber) {
        Account account = getAccount(accountNumber);
        account.lock().lock();
        try {
            if (account.isClosed()) {
                return;
            }
            if (!account.getFundsAvailable().isZero()) {
                throw new ConflictException(ExceptionMessages.ACCOUNT_NOT_EMPTY);
            }
            account.close();
            storage.remove(accountNumber, account);
        } finally {
            account.lock().unlock();
        }
    }

    @Override
    public boolean accountExist(String accountNumber) {
        return accountNumber != null && storage.containsKey(accountNumber);
    }
}
