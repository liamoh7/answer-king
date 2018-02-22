package util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidatorUtil {

    private static Validator VALIDATOR = validator();

    private ValidatorUtil() {

    }

    public static Validator validator() {
        if (VALIDATOR == null) VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
        return VALIDATOR;
    }

    public static <T> void validateWithViolation(T model) {
        validateWithViolations(model, 1);
    }

    public static <T> void validateWithNoViolations(T model) {
        final Set<ConstraintViolation<T>> violations = VALIDATOR.validate(model);
        assertTrue(violations.isEmpty());
    }

    public static <T> void validateWithViolations(T model, int violationCount) {
        final Set<ConstraintViolation<T>> violations = VALIDATOR.validate(model);
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() == violationCount);
    }
}
