package pl.kurs.security.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RoleNamePrefixValidator implements ConstraintValidator<RoleNamePrefix, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.startsWith("ROLE_");
    }
}
