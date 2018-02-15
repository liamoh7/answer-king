package answer.king.error;

public class InvalidPriceException extends Exception {
    public InvalidPriceException() {
        super("The amount provided is not valid.");
    }
}
