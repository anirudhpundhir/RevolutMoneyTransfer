package controllers;

import com.google.gson.Gson;
import entity.StandardResponse;
import entity.StatusResponse;
import service.AccountService;
import service.TransferService;
import service.impl.AccountServiceMapImpl;
import service.impl.TransferServiceMapImpl;

import static spark.Spark.*;

public class TransferController {

    public static void init() {
        TransferService transferService = TransferServiceMapImpl.getInstance();
        AccountService accountService = AccountServiceMapImpl.getInstance();

        post("/transfers", (request, response) -> {
            response.type("application/json");
            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS));
        });


        get("/transfers/:id", (request, response) -> {
            response.type("application/json");

            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(transferService.getTransfer(request.params(":id")))));
        });



    }
}
