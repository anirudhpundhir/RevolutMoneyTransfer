import currency.ExchangeRateProvider;
import currency.FixedRateProvider;
import currency.HttpExchangeRateProvider;
import entity.Account;
import entity.Money;
import entity.User;
import entity.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "4567"));
        App app = new App(rateProvider());
        initData(app);
        int bound = app.start(port);
        logger.info("Money transfer API listening on http://localhost:{}", bound);
    }

    static ExchangeRateProvider rateProvider() {
        String apiKey = System.getenv("EXCHANGE_RATES_API_KEY");
        if (apiKey != null && !apiKey.isBlank()) {
            logger.info("Using live exchange rates from exchangeratesapi.io");
            return new HttpExchangeRateProvider(apiKey);
        }
        logger.info("EXCHANGE_RATES_API_KEY not set; using fixed demo exchange rates");
        return new FixedRateProvider();
    }

    static void initData(App app) {
        User anirudh = app.users().addUser(new UserDTO("Anirudh", "Pundhir", "user@example.com"));
        Account inr = app.users().openAccount(anirudh.getId(), "My first account for savings in INR",
                Money.of("1000", "INR"));
        Account eur = app.users().openAccount(anirudh.getId(), "EUR savings for Europe trips",
                Money.of("500", "EUR"));
        app.users().addUser(new UserDTO("test", "dummy", "test@demo.com"));
        app.users().addUser(new UserDTO("John", "Hopkin", "johnhopkin@gmail.com"));
        logger.info("Seeded 3 users; demo accounts {} (INR) and {} (EUR)", inr.getAccountNumber(), eur.getAccountNumber());
    }
}
