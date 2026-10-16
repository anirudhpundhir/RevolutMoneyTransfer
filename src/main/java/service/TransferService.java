package service;

import entity.Transfer;
import entity.dto.TransferDTO;

public interface TransferService {

    /** @throws exception.NotFoundException if no such transfer */
    Transfer getTransfer(String id);

    /**
     * Atomically moves money between two accounts. Insufficient funds produce a
     * {@link Transfer.Status#CANCELLED} transfer rather than an exception.
     */
    Transfer transfer(TransferDTO request);
}
