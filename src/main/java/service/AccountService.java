package service;

import entity.Account;
import entity.Money;

import java.util.Collection;

public interface AccountService {

    Account createAccount(String name, Money initialFunds);

    Collection<Account> getAccounts();

    /** @throws exception.NotFoundException if no such account */
    Account getAccount(String accountNumber);

    Account renameAccount(String accountNumber, String newName);

    /** Deletes an account; only allowed when its balance is zero. */
    void deleteAccount(String accountNumber);

    boolean accountExist(String accountNumber);
}
