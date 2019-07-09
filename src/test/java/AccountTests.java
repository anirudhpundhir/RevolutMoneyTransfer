import entity.Account;
import entity.User;
import exception.UserException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.AccountService;
import service.UserService;
import service.impl.AccountServiceMapImpl;
import service.impl.UserServiceMapImpl;

import java.util.ArrayList;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;


public class AccountTests {
    private static UserService userService = UserServiceMapImpl.getInstance();
    private AccountService accountService = AccountServiceMapImpl.getInstance();
    private static User user;

    @BeforeAll
    public static void createUser() {
        user = userService.addUser(new User("Anirudh", "Pundhir", "gordon.pav@gmail.com"));
    }

    @Test
    public void testCreateAccount() {
        Account account = accountService.addAccount(new Account("EUR account for savings", "EUR"));
        assertThat(accountService.getAccount(account.getAccountNumber()), equalTo(account));
        accountService.deleteAccount(account.getAccountNumber());
    }

    @Test
    public void testUpdateAccount() throws UserException {
        Account account = accountService.addAccount(new Account("TEST account", "RUB"));
        account.setName("TEST2 account");
        account.setCurrency("EUR");
        accountService.editAccount(account);
        account = accountService.getAccount(account.getAccountNumber());
        assertThat(account.getName(), equalTo("TEST2 account"));
        assertThat(account.getCurrency(), equalTo("EUR"));
        accountService.deleteAccount(account.getAccountNumber());
    }

    @Test
    public void testDeleteAccount() {
        Account account = accountService.addAccount(new Account("TEST account", "RUB"));
        accountService.deleteAccount(account.getAccountNumber());
        assertNull(accountService.getAccount(account.getAccountNumber()));
    }


    @AfterAll
    public static void deleteUser() {
        userService.deleteUser(user.getId());
    }

}
