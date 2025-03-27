package pl.kurs.person.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.kurs.person.validation.CheckPersonTypeValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckPersonTypeValidator.class)
public @interface CheckPersonType {
    String message() default "UNKNOWN_PERSON_TYPE";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
