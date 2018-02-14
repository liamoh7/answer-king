package answer.king.error;

public class InvalidPaymentException extends Exception {
    public InvalidPaymentException() {
        super("The payment is not valid.");
    }
}
