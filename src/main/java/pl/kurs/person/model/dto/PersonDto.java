package pl.kurs.person.model.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import pl.kurs.person.model.Person;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "dtype")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmployeeDto.class, name = "Employee"),
        @JsonSubTypes.Type(value = RetireeDto.class, name = "Retiree"),
        @JsonSubTypes.Type(value = StudentDto.class, name = "Student")
})
public abstract class PersonDto {

    private int id;
    private String dtype;
    private String name;
    private String surname;
    private String pesel;
    private int height;
    private int weight;
    private String email;

    public PersonDto(Person person){
        this.id = person.getId();
        this.dtype = person.getDtype();
        this.name = person.getName();
        this.surname = person.getSurname();
        this.pesel = person.getPesel();
        this.height = person.getHeight();
        this.weight = person.getWeight();
        this.email = person.getEmail();
    }

}
