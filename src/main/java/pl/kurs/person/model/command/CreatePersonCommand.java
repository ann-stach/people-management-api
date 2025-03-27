package pl.kurs.person.model.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import pl.kurs.person.model.PersonParameter;
import pl.kurs.person.validation.annotation.CheckPersonType;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CreatePersonCommand {

    @CheckPersonType
    private String classType;
    private List<PersonParameter> parameters;

}
