package service.impl;

import entity.Account;
import service.AccountService;

import java.util.Collection;
import java.util.HashMap;

import constants.ExceptionMessages;

public class AccountServiceMapImpl implements AccountService {
    private HashMap<String, Account> storage;
    private static AccountServiceMapImpl instance;

    public static AccountService getInstance() {
        if (instance == null) {
            instance = new AccountServiceMapImpl();
        }
        return instance;
    }

    private AccountServiceMapImpl() {
        storage = new HashMap<>();
    }

    @Override
    public Account addAccount(Account account) {
        storage.put(account.getAccountNumber(), account);
        return account;
    }

    @Override
    public void addAll(Iterable<Account> accounts) {
        accounts.forEach(account -> storage.put(account.getAccountNumber(), account));
    }

    @Override
    public Collection<Account> getAccounts() {
        return storage.values();
    }

    @Override
    public Account getAccount(String accountNumber) {
        return storage.get(accountNumber);
    }

    @Override
    public Account editAccount(Account forEdit) throws IllegalArgumentException {
        if (forEdit.getAccountNumber() == null)
            throw new IllegalArgumentException(ExceptionMessages.ACCOUNT_NUMBER_BLANK);
        Account toEdit = storage.get(forEdit.getAccountNumber());
        if (toEdit == null)
            throw new IllegalArgumentException(ExceptionMessages.ACCOUNT_NOT_FOUND);
        if (forEdit.getName() != null) {
            toEdit.setName(forEdit.getName());
        }
        if (forEdit.getCurrency() != null) {
            toEdit.setCurrency(forEdit.getCurrency());
        }
        if (forEdit.getFundsAvailable() != null) {
            toEdit.setFundsAvailable(forEdit.getFundsAvailable());
        }

        return toEdit;
    }

    @Override
    public void deleteAccount(String accountNumber) {
        storage.remove(accountNumber);
    }

    @Override
    public boolean accountExist(String id) {
        return storage.containsKey(id);
    }

}
