package entity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class User {

    private final String id;
    private volatile String firstName;
    private volatile String lastName;
    private volatile String email;
    private final List<String> accountNumbers = new CopyOnWriteArrayList<>();

    public User(String id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
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

    public List<String> getAccountNumbers() {
        return List.copyOf(accountNumbers);
    }

    public void addAccountNumber(String accountNumber) {
        accountNumbers.add(accountNumber);
    }

    public void removeAccountNumber(String accountNumber) {
        accountNumbers.remove(accountNumber);
    }
}
