import entity.Account;
import entity.Money;
import entity.Transfer;
import entity.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.AccountService;
import service.TransferService;
import service.UserService;
import service.impl.AccountServiceMapImpl;
import service.impl.TransferServiceMapImpl;
import service.impl.UserServiceMapImpl;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class TransferTests {
    private static UserService userService = UserServiceMapImpl.getInstance();
    private static AccountService accountService = AccountServiceMapImpl.getInstance();
    private static TransferService transferService = TransferServiceMapImpl.getInstance();
    private static User user1;
    private static Account account1;
    private static Account account2;
    private static Account account3;
    private static User user2;

    @BeforeAll
    public static void init() {
        user1 = userService.addUser(new User("Ani", "Ani", "ani@gmail.com"));
        user2 = userService.addUser(new User("Sam", "Peter", "sam@gmail.com"));

        account1 = new Account("1 account", 500, "RUB");
        accountService.addAccount(account1);
        user1.addAccount(account1);

        account2 = new Account("2 account", 100, "RUB");
        accountService.addAccount(account2);
        user2.addAccount(account2);

        account3 = new Account("3 account", 100, "EUR");
        accountService.addAccount(account3);
        user2.addAccount(account2);
    }

    @Test
    public void transfer() {
        Money account1Funds = new Money(500, "RUB");
        Money account2Funds = new Money(100, "RUB");
        Money payment = new Money(100, "RUB");
        account1.setFundsAvailable(account1Funds);
        account2.setFundsAvailable(account2Funds);
        Transfer transfer = transferService.createTransfer(account1, account2, payment);
        transfer.proceed();
        assertThat(transfer.getStatus(), equalTo(Transfer.Status.PROCEEDED));
        assertThat(account1.getFundsAvailable(), equalTo(account1Funds.sub(payment)));
        assertThat(account2.getFundsAvailable(), equalTo(account2Funds.add(payment)));
        //revert
        account1.setFundsAvailable(account1Funds);
        account2.setFundsAvailable(account2Funds);
    }

    @Test
    public void transferWhenNotEnoughFunds() {
        double account1Funds = 500;
        double account2Funds = 100;
        double payment = 1000000;
        account1.setFundsAvailable(account1Funds);
        account2.setFundsAvailable(account2Funds);
        Transfer transfer = transferService.createTransfer(account1, account2, new Money(payment, "RUB"));
        transfer.proceed();
        assertThat(transfer.getStatus(), equalTo(Transfer.Status.CANCELLED));
        assertThat(account1.getFundsAvailable().getAmount(), equalTo(account1Funds));
        assertThat(account2.getFundsAvailable().getAmount(), equalTo(account2Funds));
    }

    @Test
    public void transfersInSameTime() {
        Money account1Funds = new Money(500, "RUB");
        Money account2Funds = new Money(100, "RUB");
        Money payment = new Money(300, "RUB");
        account1.setFundsAvailable(account1Funds);
        account2.setFundsAvailable(account2Funds);
        Transfer transfer = transferService.createTransfer(account1, account2, payment);
        Transfer transfer2 = transferService.createTransfer(account1, account2, payment);
        transfer.proceed();
        transfer2.proceed();
        assertThat(transfer.getStatus(), equalTo(Transfer.Status.PROCEEDED));
        assertThat(transfer2.getStatus(), equalTo(Transfer.Status.CANCELLED));
        assertThat(account1.getFundsAvailable(), equalTo(account1Funds.sub(payment)));
        assertThat(account2.getFundsAvailable(), equalTo(account2Funds.add(payment)));

        account1.setFundsAvailable(account1Funds);
        account2.setFundsAvailable(account2Funds);
    }

    @Test
    public void transfersWithDifferentCurrencies() {
        Money account1Funds = new Money(5000, "RUB");
        Money account2Funds = new Money(100, "EUR");
        Money payment = new Money(30, "EUR");
        account1.setFundsAvailable(account1Funds);
        account2.setCurrency("EUR");
        account2.setFundsAvailable(account2Funds);
        Transfer transfer = transferService.createTransfer(account1, account2, payment);
        transfer.proceed();
        assertThat(transfer.getStatus(), equalTo(Transfer.Status.PROCEEDED));
        assertThat(account1.getFundsAvailable(), equalTo(account1Funds.sub(payment)));
        assertThat(account2.getFundsAvailable(), equalTo(account2Funds.add(payment)));

        account1.setFundsAvailable(account1Funds);
        account2.setFundsAvailable(account2Funds);
    }

    @AfterAll
    public static void cleanup() {
        userService.deleteUser(user1.getId());
        userService.deleteUser(user2.getId());
        for (Transfer t : account1.getTransfers()) {
            transferService.deleteTransfer(t.getId());
        }
        for (Transfer t : account2.getTransfers()) {
            transferService.deleteTransfer(t.getId());
        }
        accountService.deleteAccount(account1.getAccountNumber());
        accountService.deleteAccount(account2.getAccountNumber());
    }
}
