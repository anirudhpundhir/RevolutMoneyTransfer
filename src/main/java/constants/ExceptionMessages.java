package constants;

public final class ExceptionMessages {

    public static final String ACCOUNT_NOT_FOUND = "Account not found";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String TRANSFER_NOT_FOUND = "Transfer not found";
    public static final String AMOUNT_NOT_POSITIVE = "Amount must be greater than zero";
    public static final String NAME_EMPTY = "Name cannot be empty";
    public static final String UNSUPPORTED_CURRENCY = "Unsupported currency";
    public static final String SAME_ACCOUNT = "Cannot transfer to the same account";
    public static final String INSUFFICIENT_FUNDS = "Insufficient funds";
    public static final String ACCOUNT_CLOSED = "Account is closed";
    public static final String ACCOUNT_NOT_EMPTY = "Account balance must be zero before it can be deleted";

    private ExceptionMessages() {
    }
}
