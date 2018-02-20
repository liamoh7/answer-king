package answer.king.error;

public class InvalidSearchCriteriaException extends Exception {
    public InvalidSearchCriteriaException() {
        super("The search criteria was invalid.");
    }
}
