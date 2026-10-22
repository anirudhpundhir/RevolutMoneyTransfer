package service.impl;

import currency.FixedRateProvider;
import entity.Account;
import entity.Money;
import entity.Transfer;
import entity.dto.TransferDTO;
import exception.ConflictException;
import exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import service.TransferService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransferServiceTest {

    private AccountServiceMapImpl accounts;
    private TransferServiceMapImpl transfers;

    @BeforeEach
    void setUp() {
        FixedRateProvider rates = new FixedRateProvider();
        accounts = new AccountServiceMapImpl(rates);
        transfers = new TransferServiceMapImpl(accounts, rates);
    }

    private Account account(String amount, String currency) {
        return accounts.createAccount("test", Money.of(amount, currency));
    }

    private static TransferDTO request(Account from, Account to, String amount, String currency) {
        return new TransferDTO(from.getAccountNumber(), to.getAccountNumber(), amount, currency);
    }

    @Test
    void movesMoneyAndRecordsHistoryOnBothAccounts() {
        Account a = account("500", "RUB");
        Account b = account("100", "RUB");

        Transfer t = transfers.transfer(request(a, b, "100", "RUB"));

        assertEquals(Transfer.Status.PROCEEDED, t.getStatus());
        assertEquals(Money.of("400", "RUB"), a.getFundsAvailable());
        assertEquals(Money.of("200", "RUB"), b.getFundsAvailable());
        assertEquals(List.of(t), a.getTransfers());
        assertEquals(List.of(t), b.getTransfers());
        assertSame(t, transfers.getTransfer(t.getId()));
    }

    @Test
    void insufficientFundsCancelsWithoutMovingMoney() {
        Account a = account("500", "RUB");
        Account b = account("100", "RUB");

        Transfer t = transfers.transfer(request(a, b, "500.01", "RUB"));

        assertEquals(Transfer.Status.CANCELLED, t.getStatus());
        assertNull(t.getDebited());
        assertEquals(Money.of("500", "RUB"), a.getFundsAvailable());
        assertEquals(Money.of("100", "RUB"), b.getFundsAvailable());
        assertEquals(1, a.getTransfers().size());
    }

    @Test
    void convertsBetweenCurrencies() {
        Account rub = account("5000", "RUB");
        Account eur = account("100", "EUR");

        Transfer t = transfers.transfer(request(rub, eur, "30", "EUR"));

        assertEquals(Money.of("3000", "RUB"), t.getDebited());
        assertEquals(Money.of("30", "EUR"), t.getCredited());
        assertEquals(Money.of("2000", "RUB"), rub.getFundsAvailable());
        assertEquals(Money.of("130", "EUR"), eur.getFundsAvailable());
    }

    @Test
    void rejectsInvalidRequests() {
        Account a = account("10", "EUR");
        Account b = account("10", "EUR");
        assertThrows(IllegalArgumentException.class, () -> transfers.transfer(request(a, a, "1", "EUR")));
        assertThrows(IllegalArgumentException.class, () -> transfers.transfer(request(a, b, "0", "EUR")));
        assertThrows(IllegalArgumentException.class, () -> transfers.transfer(request(a, b, "-1", "EUR")));
        assertThrows(IllegalArgumentException.class, () -> transfers.transfer(request(a, b, "1", "KRW")));
        assertThrows(NotFoundException.class,
                () -> transfers.transfer(new TransferDTO(a.getAccountNumber(), "nope", "1", "EUR")));
        assertTrue(a.getTransfers().isEmpty());
    }

    @Test
    void cannotDeleteAccountWithFundsOrTransferToClosedAccount() {
        Account a = account("10", "EUR");
        Account empty = account("0", "EUR");
        assertThrows(ConflictException.class, () -> accounts.deleteAccount(a.getAccountNumber()));
        accounts.deleteAccount(empty.getAccountNumber());
        assertTrue(empty.isClosed());
        assertFalse(accounts.accountExist(empty.getAccountNumber()));
    }

    /**
     * Many threads race to drain one account. With the old check-then-act code
     * this overdrew the account; now exactly balance/amount transfers succeed.
     */
    @RepeatedTest(5)
    @Timeout(30)
    void concurrentTransfersNeverOverdraw() throws Exception {
        Account source = account("1000", "EUR");
        int threads = 16;
        int attemptsPerThread = 50;
        List<Account> sinks = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            sinks.add(account("0", "EUR"));
        }

        List<Transfer> results = runConcurrently(threads, i -> () -> {
            List<Transfer> mine = new ArrayList<>();
            for (int n = 0; n < attemptsPerThread; n++) {
                mine.add(transfers.transfer(request(source, sinks.get(i), "3", "EUR")));
            }
            return mine;
        });

        long proceeded = results.stream().filter(t -> t.getStatus() == Transfer.Status.PROCEEDED).count();
        assertEquals(333, proceeded);
        assertEquals(Money.of("1", "EUR"), source.getFundsAvailable());
        Money total = source.getFundsAvailable();
        for (Account sink : sinks) {
            total = total.add(sink.getFundsAvailable());
        }
        assertEquals(Money.of("1000", "EUR"), total, "money must be conserved");
        assertEquals(threads * attemptsPerThread, source.getTransfers().size());
    }

    /** Opposing transfers (A->B and B->A) must not deadlock thanks to ordered locking. */
    @Test
    @Timeout(30)
    void opposingTransfersDoNotDeadlock() throws Exception {
        Account a = account("1000", "EUR");
        Account b = account("1000", "EUR");

        runConcurrently(8, i -> () -> {
            List<Transfer> mine = new ArrayList<>();
            for (int n = 0; n < 500; n++) {
                mine.add(i % 2 == 0
                        ? transfers.transfer(request(a, b, "1", "EUR"))
                        : transfers.transfer(request(b, a, "1", "EUR")));
            }
            return mine;
        });

        assertEquals(Money.of("2000", "EUR"), a.getFundsAvailable().add(b.getFundsAvailable()));
    }

    @Test
    void idempotencyKeyPreventsDoubleSpend() {
        Account a = account("100", "EUR");
        Account b = account("0", "EUR");

        TransferService.Result first = transfers.transfer(request(a, b, "60", "EUR"), "key-1");
        TransferService.Result retry = transfers.transfer(request(a, b, "60", "EUR"), "key-1");

        assertFalse(first.replayed());
        assertTrue(retry.replayed());
        assertSame(first.transfer(), retry.transfer());
        assertEquals(Money.of("40", "EUR"), a.getFundsAvailable());
        assertThrows(ConflictException.class, () -> transfers.transfer(request(a, b, "1", "EUR"), "key-1"));
    }

    @Test
    void failedValidationReleasesIdempotencyKey() {
        Account a = account("100", "EUR");
        Account b = account("0", "EUR");
        assertThrows(IllegalArgumentException.class, () -> transfers.transfer(request(a, b, "0", "EUR"), "k"));
        TransferService.Result fixed = transfers.transfer(request(a, b, "5", "EUR"), "k");
        assertFalse(fixed.replayed());
        assertEquals(Transfer.Status.PROCEEDED, fixed.transfer().getStatus());
    }

    @Test
    @Timeout(30)
    void concurrentRetriesWithSameKeyExecuteOnce() throws Exception {
        Account a = account("100", "EUR");
        Account b = account("0", "EUR");

        List<TransferService.Result> results = runConcurrently(16,
                i -> () -> List.of(transfers.transfer(request(a, b, "10", "EUR"), "same")));

        assertEquals(1, results.stream().filter(r -> !r.replayed()).count());
        assertEquals(Money.of("90", "EUR"), a.getFundsAvailable());
        assertEquals(1, a.getTransfers().size());
    }

    private interface TaskFactory<T> {
        Callable<List<T>> create(int index);
    }

    private static <T> List<T> runConcurrently(int threads, TaskFactory<T> factory) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Future<List<T>>> futures = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                Callable<List<T>> task = factory.create(i);
                futures.add(pool.submit(() -> {
                    start.await();
                    return task.call();
                }));
            }
            start.countDown();
            List<T> all = new ArrayList<>();
            for (Future<List<T>> f : futures) {
                all.addAll(f.get(30, TimeUnit.SECONDS));
            }
            return all;
        } finally {
            pool.shutdownNow();
        }
    }
}
