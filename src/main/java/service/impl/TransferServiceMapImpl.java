package service.impl;

import constants.ExceptionMessages;
import currency.ExchangeRateProvider;
import entity.Account;
import entity.Money;
import entity.Transfer;
import entity.dto.TransferDTO;
import exception.ConflictException;
import exception.NotFoundException;
import service.AccountService;
import service.TransferService;

import java.time.Clock;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Executes transfers atomically.
 *
 * <p>Both accounts are locked before the balance check, the debit and the
 * credit, so concurrent transfers can never overdraw an account. Locks are
 * always acquired in ascending account-number order, which rules out deadlock
 * between opposing transfers (A to B racing B to A).
 */
public class TransferServiceMapImpl implements TransferService {

    private final Map<String, Transfer> storage = new ConcurrentHashMap<>();
    private final AccountService accountService;
    private final ExchangeRateProvider rates;
    private final Clock clock;

    public TransferServiceMapImpl(AccountService accountService, ExchangeRateProvider rates) {
        this(accountService, rates, Clock.systemUTC());
    }

    public TransferServiceMapImpl(AccountService accountService, ExchangeRateProvider rates, Clock clock) {
        this.accountService = accountService;
        this.rates = rates;
        this.clock = clock;
    }

    @Override
    public Transfer getTransfer(String id) {
        Transfer transfer = id == null ? null : storage.get(id);
        if (transfer == null) {
            throw new NotFoundException(ExceptionMessages.TRANSFER_NOT_FOUND + ": " + id);
        }
        return transfer;
    }

    @Override
    public Transfer transfer(TransferDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        String currency = request.getCurrency();
        Money amount = Money.of(request.getAmount(), currency);
        if (amount.isZero()) {
            throw new IllegalArgumentException(ExceptionMessages.AMOUNT_NOT_POSITIVE);
        }
        if (!rates.supports(amount.getCurrency())) {
            throw new IllegalArgumentException(ExceptionMessages.UNSUPPORTED_CURRENCY + ": " + currency);
        }
        Account from = accountService.getAccount(request.getAccountNumberFrom());
        Account to = accountService.getAccount(request.getAccountNumberTo());
        if (from.getAccountNumber().equals(to.getAccountNumber())) {
            throw new IllegalArgumentException(ExceptionMessages.SAME_ACCOUNT);
        }

        // Convert before taking locks: a rate lookup may do I/O.
        Money debit = amount.convertTo(from.getCurrency(), rates);
        Money credit = amount.convertTo(to.getCurrency(), rates);
        if (debit.isZero() || credit.isZero()) {
            throw new IllegalArgumentException(ExceptionMessages.AMOUNT_NOT_POSITIVE + " after conversion");
        }

        Account first = from.getAccountNumber().compareTo(to.getAccountNumber()) < 0 ? from : to;
        Account second = first == from ? to : from;
        String id = UUID.randomUUID().toString();

        first.lock().lock();
        try {
            second.lock().lock();
            try {
                if (from.isClosed() || to.isClosed()) {
                    throw new ConflictException(ExceptionMessages.ACCOUNT_CLOSED);
                }
                Transfer transfer;
                if (from.getFundsAvailable().isLessThan(debit)) {
                    transfer = Transfer.cancelled(id, from.getAccountNumber(), to.getAccountNumber(), amount,
                            request.getNotes(), ExceptionMessages.INSUFFICIENT_FUNDS, clock.instant());
                } else {
                    from.withdraw(debit);
                    to.deposit(credit);
                    transfer = Transfer.proceeded(id, from.getAccountNumber(), to.getAccountNumber(), amount,
                            debit, credit, request.getNotes(), clock.instant());
                }
                from.recordTransfer(transfer);
                to.recordTransfer(transfer);
                storage.put(id, transfer);
                return transfer;
            } finally {
                second.lock().unlock();
            }
        } finally {
            first.lock().unlock();
        }
    }
}
