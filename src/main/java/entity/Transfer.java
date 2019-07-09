package entity;

import java.time.ZonedDateTime;

public class Transfer {
    private static int idSequence = 0;
    private String id;
    private Money amount;
    private Account accountFrom;
    private Account accountTo;
    private String notes;
    private Status status;
    private ZonedDateTime createdAt;


    public Transfer(Account accountFrom, Account accountTo, Money amount, String notes) {
        this.id = String.valueOf(++idSequence);
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
        this.notes = notes;
        this.createdAt = ZonedDateTime.now();
        this.status = Status.CREATED;

    }

    public Transfer(Account accountFrom, Account accountTo, Money amount) {
        this(accountFrom, accountTo, amount, "");
    }


    public void proceed() {
        Money holdAmount = amount;
        Money available = accountFrom.getFundsAvailable();
        if (amount.isGreaterThan(available)) {
            this.status = Status.CANCELLED;
            return;
        }
        accountFrom.withdrawMoney(amount);
        accountTo.addMoney(amount);
        this.status = Status.PROCEEDED;
    }

    public Money getAmount() {
        return amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public enum Status {
        CREATED, PENDING, RECEIVED, CANCELLED, PROCEEDED
    }
}
