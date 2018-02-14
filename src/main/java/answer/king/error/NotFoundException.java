package answer.king.error;

public class NotFoundException extends Exception {
    public NotFoundException() {
        super("The resource you were looking for could not be found.");
    }
}
