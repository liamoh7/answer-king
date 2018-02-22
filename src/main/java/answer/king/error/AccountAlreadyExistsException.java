package answer.king.error;

public class AccountAlreadyExistsException extends Exception {
    public AccountAlreadyExistsException() {
        super("The account already exists.");
    }
}
