package pl.kurs.security.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RoleNamePrefixValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface RoleNamePrefix {
    String message() default "Role name must start with 'ROLE_'";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
