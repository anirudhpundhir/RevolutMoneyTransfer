package controllers;

import entity.Money;
import entity.dto.AccountDTO;
import entity.dto.UserDTO;
import service.UserService;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.post;
import static spark.Spark.put;

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void register() {
        post("/users", (request, response) -> {
            response.status(201);
            return Json.success(userService.addUser(Json.body(request, UserDTO.class)));
        });

        get("/users", (request, response) -> Json.success(userService.getUsers()));

        get("/users/:id", (request, response) -> Json.success(userService.getUser(request.params(":id"))));

        get("/users/:id/accounts", (request, response) ->
                Json.success(userService.getAccounts(request.params(":id"))));

        post("/users/:id/accounts", (request, response) -> {
            AccountDTO dto = Json.body(request, AccountDTO.class);
            String initial = dto.getInitialBalance() == null ? "0" : dto.getInitialBalance();
            response.status(201);
            return Json.success(userService.openAccount(request.params(":id"), dto.getName(),
                    Money.of(initial, dto.getCurrency())));
        });

        put("/users/:id", (request, response) ->
                Json.success(userService.editUser(request.params(":id"), Json.body(request, UserDTO.class))));

        delete("/users/:id", (request, response) -> {
            userService.deleteUser(request.params(":id"));
            return Json.successMessage("user deleted");
        });

        options("/users/:id", (request, response) ->
                Json.successMessage(userService.userExist(request.params(":id"))
                        ? "User exists" : "User does not exist"));
    }
}
