package com.Revolut.service.impl;

import com.Revolut.model.Money;
import com.Revolut.model.exception.AccountLockedException;
import com.Revolut.model.exception.AccountNotFoundException;
import com.Revolut.model.exception.LimitedBalanceException;
import com.Revolut.model.exception.NegativeAmountException;
import com.Revolut.model.sql.tables.Account;
import com.Revolut.service.api.*;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.jooq.impl.DSL;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Singleton
public class MoneyServiceImpl implements MoneyService {

    final static Logger log = Logger.getLogger(MoneyServiceImpl.class);
    private DatabaseManager databaseManager;
    private AccountLockManager lockManager;
    private ExchangeService exchangeService;
    private AccountService accountService;

    @Inject
    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Inject
    public void setLockManager(AccountLockManager lockManager) {
        this.lockManager = lockManager;
    }

    @Inject
    public void setExchangeService(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @Inject
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public Money getBalance(Long id) {
        Optional<Map<String, Object>> map = databaseManager.getSqlDSL()
                .select(Account.ACCOUNT.MONEY_VALUE, Account.ACCOUNT.MONEY_CURRENCY)
                .from(Account.ACCOUNT)
                .where(Account.ACCOUNT.ID.eq(id)).fetchOptionalMap();
        if (map.isPresent()) {
            Money money = new Money();
            money.setAmount((BigDecimal) map.get().get(Account.ACCOUNT.MONEY_VALUE.getName()));
            money.setCurrency((String) map.get().get(Account.ACCOUNT.MONEY_CURRENCY.getName()));
            return money;
        } else {
            throw new AccountNotFoundException(id);
        }
    }

    @Override
    public void transfer(Long fromId, Long toId, Money moneyToTransfer) {
        if (moneyToTransfer.getAmount().signum() == -1) {
            throw new NegativeAmountException("transfer");
        }
        lockManager.doInLock(fromId, toId, () ->
                databaseManager.getSqlDSL().transaction(configuration -> {
                    checkForLock(fromId);
                    checkForLock(toId);
                    withdraw(fromId, moneyToTransfer);
                    deposit(toId, moneyToTransfer);
                })
        );
    }

    @Override
    public void withdraw(Long id, Money moneyToWithdraw) {
        if (moneyToWithdraw.getAmount().signum() == -1) {
            throw new NegativeAmountException("withdraw");
        }
        lockManager.doInLock(id, () ->
                databaseManager.getSqlDSL().transaction(configuration -> {
                    checkForLock(id);
                    Money balance = getBalance(id);
                    Money withdrawInCurrency =
                            exchangeService.exchange(moneyToWithdraw, balance.getCurrency());
                    Money newBalance = Money.subtract(balance, withdrawInCurrency);
                  /*  log.trace("withdraw: id={}, old={}, withdraw={}, withdrawInCurrency={}, new={}",
                            id, balance, moneyToWithdraw, withdrawInCurrency, newBalance);*/
                    if (newBalance.getAmount().signum() == -1) {
                        throw new LimitedBalanceException(id, "withdraw");
                    }
                    DSL.using(configuration)
                            .update(Account.ACCOUNT)
                            .set(Account.ACCOUNT.MONEY_VALUE, newBalance.getAmount())
                            .set(Account.ACCOUNT.MONEY_CURRENCY, newBalance.getCurrency())
                            .where(Account.ACCOUNT.ID.eq(id)).execute();
                }));
    }


    @Override
    public void deposit(Long id, Money moneyToDeposit) {
        if (moneyToDeposit.getAmount().signum() == -1) {
            throw new NegativeAmountException("deposit");
        }
        lockManager.doInLock(id, () ->
                databaseManager.getSqlDSL().transaction(configuration -> {
                    checkForLock(id);
                    Money balance = getBalance(id);
                    Money depositInCurrency =
                            exchangeService.exchange(moneyToDeposit, balance.getCurrency());
                    Money newBalance = Money.add(balance, depositInCurrency);
                   /* log.trace("deposit: id={}, old={}, deposit={}, depositInCurrency={}, new={}",
                            id, balance, moneyToDeposit, depositInCurrency, newBalance);*/
                    DSL.using(configuration)
                            .update(Account.ACCOUNT)
                            .set(Account.ACCOUNT.MONEY_VALUE, newBalance.getAmount())
                            .set(Account.ACCOUNT.MONEY_CURRENCY, newBalance.getCurrency())
                            .where(Account.ACCOUNT.ID.eq(id)).execute();
                }));
    }

    private void checkForLock(Long accountId) {
        if (accountService.isLocked(accountId)) {
            throw new AccountLockedException(accountId);
        }
    }


}