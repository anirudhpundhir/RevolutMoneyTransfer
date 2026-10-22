package controllers;

import exception.ConflictException;
import exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.exception;
import static spark.Spark.notFound;

/** Maps domain exceptions to HTTP status codes with a JSON error body. */
public final class ErrorHandlers {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlers.class);

    private ErrorHandlers() {
    }

    public static void register() {
        exception(IllegalArgumentException.class, (e, request, response) -> {
            response.status(400);
            response.type("application/json");
            response.body(Json.error(e.getMessage()));
        });
        exception(NotFoundException.class, (e, request, response) -> {
            response.status(404);
            response.type("application/json");
            response.body(Json.error(e.getMessage()));
        });
        exception(ConflictException.class, (e, request, response) -> {
            response.status(409);
            response.type("application/json");
            response.body(Json.error(e.getMessage()));
        });
        exception(Exception.class, (e, request, response) -> {
            logger.error("Unhandled error on {} {}", request.requestMethod(), request.pathInfo(), e);
            response.status(500);
            response.type("application/json");
            response.body(Json.error("Internal server error"));
        });
        notFound((request, response) -> {
            response.type("application/json");
            return Json.error("Route not found");
        });
    }
}
