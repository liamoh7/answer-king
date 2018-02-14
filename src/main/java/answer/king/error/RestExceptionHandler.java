package answer.king.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({InvalidPaymentException.class})
    public ResponseEntity<ErrorResponse> handleInvalidPayment(Exception e) {
        final ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ErrorResponse> handleEntityNotFound(Exception e) {
        final ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
}
