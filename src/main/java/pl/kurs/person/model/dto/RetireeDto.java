package pl.kurs.person.model.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonTypeName("retiree")
public class RetireeDto extends PersonDto {

    private int pension;
    private int yearsWorked;

    public RetireeDto(int id, String dtype, String name, String surname, String pesel, int height, int weight, String email, int pension, int yearsWorked) {
        super(id, dtype, name, surname, pesel, height, weight, email);
        this.pension = pension;
        this.yearsWorked = yearsWorked;
    }
}
