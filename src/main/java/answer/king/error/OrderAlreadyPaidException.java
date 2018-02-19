package answer.king.error;

public class OrderAlreadyPaidException extends Exception {
    public OrderAlreadyPaidException() {
        super("The order has already been paid");
    }
}
