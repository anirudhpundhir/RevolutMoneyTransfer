package service.impl;

import entity.Account;
import entity.Money;
import entity.Transfer;
import service.TransferService;

import java.util.HashMap;

public class TransferServiceMapImpl implements TransferService {
    private HashMap<String, Transfer> storage;
    private static TransferServiceMapImpl instance;

    private TransferServiceMapImpl() {
        storage = new HashMap<>();
    }

    public static TransferService getInstance() {
        if (instance == null) {
            instance = new TransferServiceMapImpl();
        }
        return instance;
    }

    @Override
    public Transfer getTransfer(String id) {
        return storage.get(id);
    }

    @Override
    public Transfer createTransfer(Account from, Account to, Money amount) {

        Transfer transfer = new Transfer(from, to, amount);
        storage.put(transfer.getId(), transfer);
        return transfer;
    }


    @Override
    public void deleteTransfer(String id) {
        storage.remove(id);
    }


}
