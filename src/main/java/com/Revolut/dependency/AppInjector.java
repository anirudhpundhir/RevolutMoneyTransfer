package com.Revolut.dependency;
import com.Revolut.service.api.*;
import com.Revolut.service.impl.*;
import com.google.inject.AbstractModule;

public class AppInjector extends AbstractModule {
    @Override
    protected void configure() {
        bind(DatabaseManager.class).to(DataManagerImpl.class);
        bind(AccountLockManager.class).to(AccountLockManagerImpl.class);
        bind(ExchangeService.class).to(ExchangeServiceImpl.class);
        bind(AccountService.class).to(AccountServiceImpl.class);
        bind(MoneyService.class).to(MoneyServiceImpl.class);

    }
}