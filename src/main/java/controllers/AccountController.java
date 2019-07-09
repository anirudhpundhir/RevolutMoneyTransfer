package controllers;

import com.google.gson.Gson;
import entity.*;
import entity.dto.TransferDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.AccountService;
import service.TransferService;
import service.impl.AccountServiceMapImpl;
import service.impl.TransferServiceMapImpl;

import static spark.Spark.*;


public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    public static void init() {
        final AccountService accountService = AccountServiceMapImpl.getInstance();
        final TransferService transferService = TransferServiceMapImpl.getInstance();

        post("/accounts", (request, response) -> {
            response.type("application/json");

            Account account = new Gson().fromJson(request.body(), Account.class);
            account = accountService.addAccount(account);

            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJson(account)));
        });

        get("/accounts", (request, response) -> {
            response.type("application/json");

            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(accountService.getAccounts())));
        });

        get("/accounts/:accountNumber", (request, response) -> {
            response.type("application/json");

            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(accountService.getAccount(request.params(":accountNumber")))));
        });

        put("/accounts/:accountNumber", (request, response) -> {
            response.type("application/json");

            Account toEdit = new Gson().fromJson(request.body(), Account.class);
            Account editedAccount = accountService.editAccount(toEdit);

            if (editedAccount != null) {
                return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJson(editedAccount)));
            } else {
                return new Gson().toJson(new StandardResponse(StatusResponse.ERROR, new Gson().toJson("Account not found or error in edit")));
            }
        });


        get("/accounts/:accountNumber/transfers", (request, response) -> {
            response.type("application/json");
            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJson(
                    accountService.getAccount(request.params(":accountNumber")).getTransfers()
            )));
        });

        post("/accounts/:accountNumber/transfers", (request, response) -> {
            response.type("application/json");
            TransferDTO transferDTO = new Gson().fromJson(request.body(), TransferDTO.class);
            Account from = accountService.getAccount(request.params(":accountNumber"));
            Account to = accountService.getAccount(transferDTO.getAccountNumberTo());
            if (from == null || to == null) {
                return new Gson().toJson(new StandardResponse(StatusResponse.ERROR, new Gson().toJson("Wrong account number")));
            }
            if (!Money.Currencies.contains(transferDTO.getCurrency())) {
                return new Gson().toJson(new StandardResponse(StatusResponse.ERROR, new Gson().toJson("Wrong currency")));
            }
            Money amount;
            double d;
            try {
                d = Double.parseDouble(transferDTO.getAmount());
                amount = new Money(d, transferDTO.getCurrency());
            } catch (IllegalArgumentException e) {
                return new Gson().toJson(new StandardResponse(StatusResponse.ERROR, new Gson().toJson("Wrong amount")));
            }

            Transfer t = transferService.createTransfer(from, to, amount);
            t.proceed();
            if (t.getStatus().equals(Transfer.Status.PROCEEDED))
                return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(t)));
            else
                return new Gson().toJson(new StandardResponse(StatusResponse.ERROR, new Gson().toJsonTree(t.getStatus())));


        });

        delete("/accounts/:accountNumber", (request, response) ->

        {
            response.type("application/json");

            accountService.deleteAccount(request.params(":accountNumber"));
            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, "account deleted"));
        });

        options("/accounts/:accountNumber", (request, response) ->

        {
            response.type("application/json");

            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, (accountService.accountExist(request.params(":accountNumber"))) ? "Account exists" : "Account does not exists"));
        });

    }


}
