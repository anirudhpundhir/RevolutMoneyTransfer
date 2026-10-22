package entity;

import java.time.Instant;

/**
 * Immutable record of a transfer attempt. Accounts are referenced by number
 * (not by object) so serialising an account's history cannot recurse.
 */
public final class Transfer {

    public enum Status {
        PROCEEDED, CANCELLED
    }

    private final String id;
    private final String accountFrom;
    private final String accountTo;
    private final Money amount;
    private final Money debited;
    private final Money credited;
    private final String notes;
    private final Status status;
    private final String failureReason;
    private final String createdAt;

    private Transfer(String id, String accountFrom, String accountTo, Money amount, Money debited,
                     Money credited, String notes, Status status, String failureReason, Instant createdAt) {
        this.id = id;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
        this.debited = debited;
        this.credited = credited;
        this.notes = notes;
        this.status = status;
        this.failureReason = failureReason;
        this.createdAt = createdAt.toString();
    }

    public static Transfer proceeded(String id, String from, String to, Money amount,
                                     Money debited, Money credited, String notes, Instant at) {
        return new Transfer(id, from, to, amount, debited, credited, notes, Status.PROCEEDED, null, at);
    }

    public static Transfer cancelled(String id, String from, String to, Money amount,
                                     String notes, String reason, Instant at) {
        return new Transfer(id, from, to, amount, null, null, notes, Status.CANCELLED, reason, at);
    }

    public String getId() {
        return id;
    }

    public String getAccountFrom() {
        return accountFrom;
    }

    public String getAccountTo() {
        return accountTo;
    }

    public Money getAmount() {
        return amount;
    }

    /** Amount taken from the source account, in its currency; null if cancelled. */
    public Money getDebited() {
        return debited;
    }

    /** Amount added to the destination account, in its currency; null if cancelled. */
    public Money getCredited() {
        return credited;
    }

    public String getNotes() {
        return notes;
    }

    public Status getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
