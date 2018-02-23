package answer.king.error;

public class InvalidCriteriaException extends Exception {
    public InvalidCriteriaException() {
        super("The criteria was invalid.");
    }
}
