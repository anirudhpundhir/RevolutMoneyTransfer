package com.Revolut.dependency;
import com.google.inject.AbstractModule;
import com.revolut.task.service.api.*;
import com.revolut.task.service.impl.*;

public class AppInjector extends AbstractModule {
    @Override
    protected void configure() {
        bind(DatabaseManager.class).to(DatabaseManagerImpl.class);
        bind(AccountLockManager.class).to(AccountLockManagerImpl.class);
        bind(ExchangeService.class).to(ExchangeServiceImpl.class);
        bind(AccountService.class).to(AccountServiceImpl.class);
        bind(MoneyService.class).to(MoneyServiceImpl.class);

    }
}