package entity;

import constants.ExceptionMessages;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A single-currency account.
 *
 * <p>The balance and the closed flag are guarded by {@link #lock()}. Mutating
 * methods must be called with that lock held; see
 * {@link service.impl.TransferServiceMapImpl} for the lock-ordering protocol.
 */
public class Account {

    private final String accountNumber;
    private final String currency;
    private volatile String name;
    private volatile Money fundsAvailable;
    private volatile boolean closed;
    private final List<Transfer> transfers = new CopyOnWriteArrayList<>();

    private final transient ReentrantLock lock = new ReentrantLock();

    public Account(String accountNumber, String name, Money initialFunds) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(ExceptionMessages.NAME_EMPTY);
        }
        this.accountNumber = accountNumber;
        this.name = name;
        this.currency = initialFunds.getCurrency();
        this.fundsAvailable = initialFunds;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(ExceptionMessages.NAME_EMPTY);
        }
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public Money getFundsAvailable() {
        return fundsAvailable;
    }

    /** Transfer history, oldest first. Read-only view. */
    public List<Transfer> getTransfers() {
        return List.copyOf(transfers);
    }

    public boolean isClosed() {
        return closed;
    }

    public ReentrantLock lock() {
        return lock;
    }

    public void withdraw(Money amount) {
        requireLocked();
        fundsAvailable = fundsAvailable.subtract(amount);
    }

    public void deposit(Money amount) {
        requireLocked();
        fundsAvailable = fundsAvailable.add(amount);
    }

    public void recordTransfer(Transfer transfer) {
        requireLocked();
        transfers.add(transfer);
    }

    public void close() {
        requireLocked();
        closed = true;
    }

    private void requireLocked() {
        if (!lock.isHeldByCurrentThread()) {
            throw new IllegalStateException("Account lock must be held");
        }
    }
}
