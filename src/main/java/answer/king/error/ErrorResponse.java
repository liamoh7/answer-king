package answer.king.error;

import java.util.Objects;

public final class ErrorResponse {

    private final int statusCode;
    private final String message;
    private final Exception ex;

    public ErrorResponse(int statusCode, String message, Exception ex) {
        this.statusCode = statusCode;
        this.message = message;
        this.ex = ex;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public Exception getEx() {
        return ex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ErrorResponse)) return false;
        ErrorResponse that = (ErrorResponse) o;
        return statusCode == that.statusCode &&
                Objects.equals(message, that.message) &&
                Objects.equals(ex, that.ex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, message, ex);
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", ex=" + ex +
                '}';
    }
}
