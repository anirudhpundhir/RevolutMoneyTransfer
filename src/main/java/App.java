import controllers.AccountController;
import controllers.ErrorHandlers;
import controllers.TransferController;
import controllers.UserController;
import currency.ExchangeRateProvider;
import service.AccountService;
import service.TransferService;
import service.UserService;
import service.impl.AccountServiceMapImpl;
import service.impl.TransferServiceMapImpl;
import service.impl.UserServiceMapImpl;
import spark.Spark;

/** Wires services and HTTP routes. One instance per JVM (Spark's server is static). */
public final class App {

    private final AccountService accountService;
    private final UserService userService;
    private final TransferService transferService;

    public App(ExchangeRateProvider rates) {
        this.accountService = new AccountServiceMapImpl(rates);
        this.userService = new UserServiceMapImpl(accountService);
        this.transferService = new TransferServiceMapImpl(accountService, rates);
    }

    /** Starts the HTTP server; pass port 0 for a random free port. Returns the bound port. */
    public int start(int port) {
        Spark.port(port);
        Spark.before((request, response) -> response.type("application/json"));
        ErrorHandlers.register();
        new UserController(userService).register();
        new AccountController(accountService, transferService).register();
        new TransferController(transferService).register();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public AccountService accounts() {
        return accountService;
    }

    public UserService users() {
        return userService;
    }

    public TransferService transfers() {
        return transferService;
    }
}
