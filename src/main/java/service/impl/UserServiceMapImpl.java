package service.impl;

import constants.ExceptionMessages;
import entity.Account;
import entity.Money;
import entity.User;
import entity.dto.UserDTO;
import exception.NotFoundException;
import service.AccountService;
import service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserServiceMapImpl implements UserService {

    static final String DEFAULT_ACCOUNT_CURRENCY = "RUB";

    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong();
    private final AccountService accountService;

    public UserServiceMapImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public User addUser(UserDTO dto) {
        if (dto == null || dto.getFirstName() == null || dto.getFirstName().isBlank()) {
            throw new IllegalArgumentException(ExceptionMessages.NAME_EMPTY);
        }
        User user = new User(String.valueOf(idSequence.incrementAndGet()),
                dto.getFirstName(), dto.getLastName(), dto.getEmail());
        Account defaultAccount = accountService.createAccount("Default account", Money.zero(DEFAULT_ACCOUNT_CURRENCY));
        user.addAccountNumber(defaultAccount.getAccountNumber());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public User getUser(String id) {
        User user = id == null ? null : users.get(id);
        if (user == null) {
            throw new NotFoundException(ExceptionMessages.USER_NOT_FOUND + ": " + id);
        }
        return user;
    }

    @Override
    public List<Account> getAccounts(String userId) {
        return getUser(userId).getAccountNumbers().stream()
                .filter(accountService::accountExist)
                .map(accountService::getAccount)
                .toList();
    }

    @Override
    public Account openAccount(String userId, String name, Money initialFunds) {
        User user = getUser(userId);
        Account account = accountService.createAccount(name, initialFunds);
        user.addAccountNumber(account.getAccountNumber());
        return account;
    }

    @Override
    public User editUser(String id, UserDTO changes) {
        User user = getUser(id);
        if (changes.getFirstName() != null) {
            user.setFirstName(changes.getFirstName());
        }
        if (changes.getLastName() != null) {
            user.setLastName(changes.getLastName());
        }
        if (changes.getEmail() != null) {
            user.setEmail(changes.getEmail());
        }
        return user;
    }

    @Override
    public void deleteUser(String id) {
        if (users.remove(id) == null) {
            throw new NotFoundException(ExceptionMessages.USER_NOT_FOUND + ": " + id);
        }
    }

    @Override
    public boolean userExist(String id) {
        return id != null && users.containsKey(id);
    }
}
