package entity.dto;

/** Request body for creating a transfer. {@code amount} is a decimal string, e.g. "10.50". */
public class TransferDTO {
    private String accountNumberFrom;
    private String accountNumberTo;
    private String amount;
    private String currency;
    private String notes;

    public TransferDTO() {
    }

    public TransferDTO(String accountNumberFrom, String accountNumberTo, String amount, String currency) {
        this.accountNumberFrom = accountNumberFrom;
        this.accountNumberTo = accountNumberTo;
        this.amount = amount;
        this.currency = currency;
    }

    public String getAccountNumberFrom() {
        return accountNumberFrom;
    }

    public void setAccountNumberFrom(String accountNumberFrom) {
        this.accountNumberFrom = accountNumberFrom;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
