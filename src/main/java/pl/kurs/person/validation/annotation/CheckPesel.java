package pl.kurs.person.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.kurs.person.validation.CheckPeselValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckPeselValidator.class)
public @interface CheckPesel {
    String message() default "INVALID_PESEL";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
