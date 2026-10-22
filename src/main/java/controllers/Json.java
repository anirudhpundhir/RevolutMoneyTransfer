package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import entity.StandardResponse;
import entity.StatusResponse;
import spark.Request;

/** Shared JSON helpers for controllers. */
final class Json {

    static final Gson GSON = new Gson();

    private Json() {
    }

    static <T> T body(Request request, Class<T> type) {
        try {
            T value = GSON.fromJson(request.body(), type);
            if (value == null) {
                throw new IllegalArgumentException("Request body is required");
            }
            return value;
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("Malformed JSON body");
        }
    }

    static String success(Object data) {
        return GSON.toJson(new StandardResponse(StatusResponse.SUCCESS, GSON.toJsonTree(data)));
    }

    static String successMessage(String message) {
        return GSON.toJson(new StandardResponse(StatusResponse.SUCCESS, message));
    }

    static String error(String message) {
        return GSON.toJson(new StandardResponse(StatusResponse.ERROR, message));
    }

    static String error(String message, Object data) {
        StandardResponse response = new StandardResponse(StatusResponse.ERROR, GSON.toJsonTree(data));
        response.setMessage(message);
        return GSON.toJson(response);
    }
}
