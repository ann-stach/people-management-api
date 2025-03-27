package pl.kurs.position.model.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pl.kurs.position.model.Position;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PositionDto {

    private int id;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String name;
    private int salary;
    private Integer employee_id;


    public static PositionDto toDto(Position position) {;
        Integer employeeId;
        if (position.getEmployee() != null) {
            employeeId = position.getEmployee().getId();
        } else {
            employeeId = null;
        }

        return new PositionDto(
                position.getId(),
                position.getDateFrom(),
                position.getDateTo(),
                position.getName(),
                position.getSalary(),
                employeeId);
    }

}
