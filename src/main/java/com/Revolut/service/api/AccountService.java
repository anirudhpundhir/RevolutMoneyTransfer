package com.Revolut.service.api;


import com.Revolut.model.Account;

public interface AccountService {
    Account createAccount();
    Account createAccount(String currency);
    Account getAccount(Long id);
    boolean isLocked(Long id);
    void removeAccount(Long id);
    void lockAccount(Long id);
    void unlockAccount(Long id);
}