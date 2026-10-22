package controllers;

import entity.Transfer;
import entity.dto.TransferDTO;
import service.TransferService;
import spark.Request;
import spark.Response;

import static spark.Spark.get;
import static spark.Spark.post;

public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    public void register() {
        post("/transfers", (request, response) ->
                execute(transferService, Json.body(request, TransferDTO.class), request, response));

        get("/transfers/:id", (request, response) ->
                Json.success(transferService.getTransfer(request.params(":id"))));
    }

    static final String IDEMPOTENCY_HEADER = "Idempotency-Key";

    static String execute(TransferService service, TransferDTO dto, Request request, Response response) {
        TransferService.Result result = service.transfer(dto, request.headers(IDEMPOTENCY_HEADER));
        if (result.replayed()) {
            response.header("Idempotent-Replayed", "true");
        }
        return respond(result.transfer(), response);
    }

    static String respond(Transfer transfer, Response response) {
        if (transfer.getStatus() == Transfer.Status.PROCEEDED) {
            response.status(201);
            return Json.success(transfer);
        }
        response.status(422);
        return Json.error(transfer.getFailureReason(), transfer);
    }
}
