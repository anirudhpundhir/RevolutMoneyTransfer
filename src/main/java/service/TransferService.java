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

    /**
     * Like {@link #transfer(TransferDTO)}, but if {@code idempotencyKey} has been
     * seen before the original transfer is returned instead of moving money again.
     * Reusing a key with a different request is rejected.
     *
     * @param idempotencyKey client-supplied key, or null to disable deduplication
     */
    Result transfer(TransferDTO request, String idempotencyKey);

    record Result(Transfer transfer, boolean replayed) {
    }
}
