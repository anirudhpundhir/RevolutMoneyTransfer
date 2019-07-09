package service;

import entity.Account;

import java.util.Collection;

public interface AccountService {
    public Account addAccount(Account account);

    public void addAll(Iterable<Account> accounts);

    public Collection<Account> getAccounts();

    public Account getAccount(String id);

    public Account editAccount(Account user)
            throws exception.UserException;

    public void deleteAccount(String id);

    public boolean accountExist(String id);
}
