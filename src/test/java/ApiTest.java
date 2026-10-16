import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import currency.FixedRateProvider;
import entity.Account;
import entity.Money;
import entity.User;
import entity.dto.UserDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** End-to-end tests against the real HTTP server on a random port. */
class ApiTest {

    private static App app;
    private static String baseUrl;
    private static final HttpClient http = HttpClient.newHttpClient();

    @BeforeAll
    static void start() {
        app = new App(new FixedRateProvider());
        baseUrl = "http://localhost:" + app.start(0);
    }

    @AfterAll
    static void stop() {
        app.stop();
    }

    private static HttpResponse<String> send(String method, String path, String body, String... headers)
            throws IOException, InterruptedException {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(body));
        if (headers.length > 0) {
            b.headers(headers);
        }
        return http.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static JsonObject json(HttpResponse<String> response) {
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    @Test
    void createsUserWithDefaultAccount() throws Exception {
        HttpResponse<String> created = send("POST", "/users",
                "{\"firstName\":\"Ada\",\"lastName\":\"Lovelace\",\"email\":\"ada@example.com\"}");
        assertEquals(201, created.statusCode());
        String id = json(created).getAsJsonObject("data").get("id").getAsString();

        HttpResponse<String> accounts = send("GET", "/users/" + id + "/accounts", null);
        assertEquals(200, accounts.statusCode());
        assertEquals(1, json(accounts).getAsJsonArray("data").size());
    }

    @Test
    void transferEndpointIsRegisteredAndIdempotent() throws Exception {
        Account from = app.accounts().createAccount("from", Money.of("100", "EUR"));
        Account to = app.accounts().createAccount("to", Money.of("0", "USD"));
        String body = "{\"accountNumberFrom\":\"" + from.getAccountNumber() + "\",\"accountNumberTo\":\""
                + to.getAccountNumber() + "\",\"amount\":\"25.00\",\"currency\":\"EUR\"}";

        HttpResponse<String> first = send("POST", "/transfers", body, "Idempotency-Key", "abc-123");
        HttpResponse<String> retry = send("POST", "/transfers", body, "Idempotency-Key", "abc-123");

        assertEquals(201, first.statusCode(), first.body());
        assertEquals(201, retry.statusCode());
        assertEquals("true", retry.headers().firstValue("Idempotent-Replayed").orElse(null));
        String id = json(first).getAsJsonObject("data").get("id").getAsString();
        assertEquals(id, json(retry).getAsJsonObject("data").get("id").getAsString());
        assertEquals(Money.of("75", "EUR"), from.getFundsAvailable());
        assertEquals(Money.of("27", "USD"), to.getFundsAvailable());

        HttpResponse<String> fetched = send("GET", "/transfers/" + id, null);
        assertEquals(200, fetched.statusCode());
        HttpResponse<String> history = send("GET", "/accounts/" + from.getAccountNumber() + "/transfers", null);
        assertEquals(1, json(history).getAsJsonArray("data").size());
    }

    @Test
    void accountScopedTransferReportsInsufficientFunds() throws Exception {
        Account from = app.accounts().createAccount("poor", Money.of("1", "EUR"));
        Account to = app.accounts().createAccount("rich", Money.of("0", "EUR"));

        HttpResponse<String> response = send("POST", "/accounts/" + from.getAccountNumber() + "/transfers",
                "{\"accountNumberTo\":\"" + to.getAccountNumber() + "\",\"amount\":\"2\",\"currency\":\"EUR\"}");

        assertEquals(422, response.statusCode());
        assertEquals("CANCELLED", json(response).getAsJsonObject("data").get("status").getAsString());
    }

    @Test
    void errorsMapToHttpStatusCodes() throws Exception {
        assertEquals(404, send("GET", "/accounts/does-not-exist", null).statusCode());
        assertEquals(404, send("GET", "/transfers/does-not-exist", null).statusCode());
        assertEquals(400, send("POST", "/transfers", "{not json").statusCode());
        assertEquals(400, send("POST", "/accounts", "{\"name\":\"x\",\"currency\":\"EUR\",\"initialBalance\":\"-5\"}")
                .statusCode());

        User user = app.users().addUser(new UserDTO("Del", "Me", "d@example.com"));
        Account funded = app.users().openAccount(user.getId(), "funded", Money.of("5", "EUR"));
        assertEquals(409, send("DELETE", "/accounts/" + funded.getAccountNumber(), null).statusCode());
        assertTrue(app.accounts().accountExist(funded.getAccountNumber()));
    }
}
