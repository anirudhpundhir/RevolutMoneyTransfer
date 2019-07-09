package service;

import entity.Account;
import entity.Money;
import entity.Transfer;


public interface TransferService {
    Transfer getTransfer(String id);

    Transfer createTransfer(Account from, Account to, Money amount);

    void deleteTransfer(String id);
}
