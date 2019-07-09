package com.Revolut.service.impl;

import com.Revolut.model.Money;
import com.Revolut.model.exception.AccountNotFoundException;
import com.Revolut.model.sql.tables.Account;
import com.Revolut.model.sql.tables.AccountRecord;
import com.Revolut.service.api.AccountService;
import com.Revolut.service.api.DatabaseManager;
import com.Revolut.service.api.ExchangeService;
import com.google.inject.Singleton;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import javax.inject.Inject;

@Singleton
public class AccountServiceImpl implements AccountService {
    private DatabaseManager databaseManager;
    private ExchangeService exchangeService;


    @Inject
    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Inject
    public void setExchangeService(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @Override
    public com.Revolut.model.Account createAccount() {
        AccountRecord record = databaseManager.getSqlDSL()
                .insertInto(Account.ACCOUNT)
                .defaultValues()
                .returning().fetchOne();
        return convertFrom(record);
    }

    @Override
    public com.Revolut.model.Account createAccount(String currency) {
        if (!exchangeService.isCurrencySupported(currency)) {
            throw new IllegalArgumentException("Currency " + currency + " is not supported");
        }
        AccountRecord record = databaseManager.getSqlDSL()
                .insertInto(Account.ACCOUNT, Account.ACCOUNT.MONEY_CURRENCY)
                .values(currency)
                .returning().fetchOne();
        return convertFrom(record);
    }

    @Override
    public com.Revolut.model.Account getAccount(Long id) {
        AccountRecord accountRecord = databaseManager.getSqlDSL()
                .selectFrom(Account.ACCOUNT)
                .where(Account.ACCOUNT.ID.eq(id))
                .fetchOne();
        if (accountRecord != null) {
            return convertFrom(accountRecord);
        }
        throw new AccountNotFoundException(id);
    }

    @Override
    public boolean isLocked(Long id) {
        Record1<Boolean> locked = databaseManager.getSqlDSL()
                .select(Account.ACCOUNT.LOCKED).from(Account.ACCOUNT)
                .where(Account.ACCOUNT.ID.eq(id))
                .fetchOne();
        if (locked == null) {
            throw new AccountNotFoundException(id);
        }
        return locked.get(Account.ACCOUNT.LOCKED);
    }

    @Override
    public void removeAccount(Long id) {
        boolean accountExists = databaseManager.getSqlDSL()
                .deleteFrom(Account.ACCOUNT)
                .where(Account.ACCOUNT.ID.eq(id))
                .execute() > 0;
        if (!accountExists) {
            throw new AccountNotFoundException(id);
        }
    }

    @Override
    public void lockAccount(Long id) {
        DSLContext sql = databaseManager.getSqlDSL();
        boolean accountExists = sql.transactionResult(configuration ->
                DSL.using(configuration)
                        .update(Account.ACCOUNT)
                        .set(Account.ACCOUNT.LOCKED, true)
                        .where(Account.ACCOUNT.ID.eq(id))
                        .execute() > 0);
        if (!accountExists) {
            throw new AccountNotFoundException(id);
        }
    }

    @Override
    public void unlockAccount(Long id) {
        DSLContext sql = databaseManager.getSqlDSL();
        boolean accountExists = sql.transactionResult(configuration ->
                DSL.using(configuration)
                        .update(Account.ACCOUNT)
                        .set(Account.ACCOUNT.LOCKED, false)
                        .where(Account.ACCOUNT.ID.eq(id))
                        .execute() > 0);
        if (!accountExists) {
            throw new AccountNotFoundException(id);
        }
    }

    private com.Revolut.model.Account convertFrom(AccountRecord accountRecord) {
        com.Revolut.model.Account account = new com.Revolut.model.Account();
        account.setId(accountRecord.getId());
        account.setLocked(accountRecord.getLocked());
        Money balance = new Money();
        balance.setAmount(accountRecord.getMoneyValue());
        balance.setCurrency(accountRecord.getMoneyCurrency());
        account.setBalance(balance);
        return account;
    }


}