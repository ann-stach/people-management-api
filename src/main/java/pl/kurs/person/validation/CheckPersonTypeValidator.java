package pl.kurs.person.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import pl.kurs.person.facades.personFacade.PersonFacade;
import pl.kurs.person.validation.annotation.CheckPersonType;

import java.util.Map;


@RequiredArgsConstructor
public class CheckPersonTypeValidator implements ConstraintValidator<CheckPersonType, String> {

    private final Map<String, PersonFacade> facades;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return facades.containsKey(value.toLowerCase() + "Facade");
    }
}
