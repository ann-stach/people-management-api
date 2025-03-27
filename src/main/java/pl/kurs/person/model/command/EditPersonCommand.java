package pl.kurs.person.model.command;

import lombok.*;
import pl.kurs.person.model.PersonParameter;
import pl.kurs.person.validation.annotation.CheckPersonType;

import java.util.List;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EditPersonCommand {

    @CheckPersonType
    private String classType;
    private List<PersonParameter> parameters;
    private Long version;

}
