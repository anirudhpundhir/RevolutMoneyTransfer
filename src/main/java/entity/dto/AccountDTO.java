package entity.dto;

/** Request body for creating or renaming an account. */
public class AccountDTO {
    private String name;
    private String currency;
    private String initialBalance;

    public AccountDTO() {
    }

    public AccountDTO(String name, String currency, String initialBalance) {
        this.name = name;
        this.currency = currency;
        this.initialBalance = initialBalance;
    }

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    public String getInitialBalance() {
        return initialBalance;
    }
}
