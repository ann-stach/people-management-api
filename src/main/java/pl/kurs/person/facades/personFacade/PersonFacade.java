package pl.kurs.person.facades.personFacade;


import pl.kurs.person.model.Person;
import pl.kurs.person.model.PersonParameter;
import pl.kurs.person.model.dto.PersonDto;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface PersonFacade<ENTITY extends Person, DTO extends PersonDto> {

    default ENTITY createPerson(List<PersonParameter> parameters) {
        return createPersonInternal(parameters.stream().collect(Collectors.toMap(PersonParameter::getName, PersonParameter::getValue)));
    }

    default ENTITY editPerson(ENTITY entity, List<PersonParameter> parameters, long version) {

        return editPersonInternal(entity, parameters.stream()
                .collect(Collectors.toMap(PersonParameter::getName, PersonParameter::getValue)), version);
    }


    default <T> void updateField(Map<String, String> parameters, String key, Consumer<String> setter) {
        if (parameters.containsKey(key)) {
            setter.accept(parameters.get(key));
        }
    }

    ENTITY editPersonInternal(ENTITY entity, Map<String, String> parameters, long version);


    ENTITY clone(ENTITY entity);

    ENTITY createPersonInternal(Map<String, String> parameters);

    DTO toDto(ENTITY entity);

    String[] getSqlArgs(String[] args);
    String getSql();


}
