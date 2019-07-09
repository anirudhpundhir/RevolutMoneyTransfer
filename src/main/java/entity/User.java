package entity;

import service.impl.AccountServiceMapImpl;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private List<Account> accounts;

    // constructors, getters and setters

    public User(String firstName, String lastName, String email, List<Account> accounts) {
        this(firstName, lastName, email);
        this.accounts = accounts;
    }

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        Account account = new Account("Default account", 0, "RUB");
        AccountServiceMapImpl.getInstance().addAccount(account);
        this.accounts = new ArrayList<>();
        accounts.add(account);
    }


    public String getId() {
        return id;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    public void setId(String id) {
        this.id = id;
    }
}