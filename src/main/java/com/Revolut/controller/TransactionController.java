package com.Revolut.controller;

import com.Revolut.dependency.InjectorProvider;
import com.Revolut.model.Money;
import com.Revolut.service.api.MoneyService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/account")
public class TransactionController {
    private final MoneyService moneyService;

    public TransactionController() {
        moneyService = InjectorProvider.provide().getInstance(MoneyService.class);
    }

    @POST
    @Path("{id: [0-9]+}/deposit")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deposit(@PathParam("id") Long accountId, Money money) {
        moneyService.deposit(accountId, money);
        return Response.noContent().build();
    }

    @POST
    @Path("{id: [0-9]+}/withdraw")
    @Produces(MediaType.APPLICATION_JSON)
    public Response withdraw(@PathParam("id") Long accountId, Money money) {
        moneyService.withdraw(accountId, money);
        return Response.noContent().build();
    }

    @POST
    @Path("{fromId: [0-9]+}/transfer/{toId: [0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response transfer(
            @PathParam("fromId") Long fromId,
            @PathParam("toId") Long toId,
            Money money) {
        moneyService.transfer(fromId, toId, money);
        return Response.noContent().build();
    }


}