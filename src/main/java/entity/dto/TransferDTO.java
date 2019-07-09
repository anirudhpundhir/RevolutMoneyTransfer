package entity.dto;

public class TransferDTO {
    private String accountNumberTo;
    private String amount;
    private String currency;

    public TransferDTO(String accountNumberTo, String amount, String currency) {
        this.accountNumberTo = accountNumberTo;
        this.amount = amount;
        this.currency = currency;
    }

    public String getAccountNumberTo() {
        return accountNumberTo;
    }

    public void setAccountNumberTo(String accountNumberTo) {
        this.accountNumberTo = accountNumberTo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
