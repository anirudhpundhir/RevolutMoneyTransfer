import controllers.AccountController;
import controllers.UserController;
import entity.Account;
import entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.AccountService;
import service.UserService;
import service.impl.AccountServiceMapImpl;
import service.impl.UserServiceMapImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        UserController.init();
        AccountController.init();
        initData();
    }

    public static void initData() {
        UserService userService = UserServiceMapImpl.getInstance();
        AccountService accountService = AccountServiceMapImpl.getInstance();
        List<Account> accounts = new ArrayList<>(Arrays.asList(
                new Account("My first account for savings in INR", 1000, "INR"),
                new Account("EUR savings for Europe trips", 500,"EUR")
        ));
        accountService.addAll(accounts);
        User user = new User("Anirudh", "Pundhir", "anirudhpundhir@gmail.com", accounts);
        userService.addUser(user);
        user = new User("test", "dummy", "test@demo.com");
        userService.addUser(user);
        user = new User("John", "Hopkin", "johnhopkin@gmail.com");
        userService.addUser(user);

        logger.info("Created 3 users");


    }
}
