package pl.kurs.person.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.kurs.person.validation.annotation.CheckPesel;

public class CheckPeselValidator implements ConstraintValidator<CheckPesel, String> {

    @Override
    public boolean isValid(String pesel, ConstraintValidatorContext constraintValidatorContext) {
        if (pesel == null || pesel.length() != 11 || !pesel.matches("\\d{11}")) {
            return false;
        }

        int[] weights = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3, 1};
        int sum = 0;
        for (int i = 0; i < 11; i++) {
            sum += Character.getNumericValue(pesel.charAt(i)) * weights[i];
        }

        return sum % 10 == 0;
    }
}
