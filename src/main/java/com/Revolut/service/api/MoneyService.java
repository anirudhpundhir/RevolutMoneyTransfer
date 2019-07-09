package com.Revolut.service.api;


import com.Revolut.model.Money;

public interface MoneyService {
    Money getBalance(Long id);

    void transfer(Long fromId, Long toId, Money moneyToTransfer);

    void withdraw(Long id, Money moneyToWithdraw);

    void deposit(Long id, Money moneyToDeposit);
}