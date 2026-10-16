package controllers;

import entity.Money;
import entity.dto.AccountDTO;
import entity.dto.TransferDTO;
import service.AccountService;
import service.TransferService;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.post;
import static spark.Spark.put;

public class AccountController {

    private final AccountService accountService;
    private final TransferService transferService;

    public AccountController(AccountService accountService, TransferService transferService) {
        this.accountService = accountService;
        this.transferService = transferService;
    }

    public void register() {
        post("/accounts", (request, response) -> {
            AccountDTO dto = Json.body(request, AccountDTO.class);
            String initial = dto.getInitialBalance() == null ? "0" : dto.getInitialBalance();
            response.status(201);
            return Json.success(accountService.createAccount(dto.getName(), Money.of(initial, dto.getCurrency())));
        });

        get("/accounts", (request, response) -> Json.success(accountService.getAccounts()));

        get("/accounts/:accountNumber", (request, response) ->
                Json.success(accountService.getAccount(request.params(":accountNumber"))));

        // Only the name is editable. Balances change exclusively through transfers.
        put("/accounts/:accountNumber", (request, response) -> {
            AccountDTO dto = Json.body(request, AccountDTO.class);
            return Json.success(accountService.renameAccount(request.params(":accountNumber"), dto.getName()));
        });

        get("/accounts/:accountNumber/transfers", (request, response) ->
                Json.success(accountService.getAccount(request.params(":accountNumber")).getTransfers()));

        post("/accounts/:accountNumber/transfers", (request, response) -> {
            TransferDTO dto = Json.body(request, TransferDTO.class);
            dto.setAccountNumberFrom(request.params(":accountNumber"));
            return TransferController.execute(transferService, dto, request, response);
        });

        delete("/accounts/:accountNumber", (request, response) -> {
            accountService.deleteAccount(request.params(":accountNumber"));
            return Json.successMessage("account deleted");
        });

        options("/accounts/:accountNumber", (request, response) ->
                Json.successMessage(accountService.accountExist(request.params(":accountNumber"))
                        ? "Account exists" : "Account does not exist"));
    }
}
