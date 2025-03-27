package pl.kurs.person.model.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.person.model.Employee;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName("employee")
public class EmployeeDto extends PersonDto {

    private LocalDate employedFrom;
    private String position;
    private int salary;
    private Long numberOfPositions;

    public EmployeeDto(int id, String dtype, String name, String surname, String pesel, int height, int weight, String email, LocalDate employedFrom, String position, int salary, Long numberOfPositions) {
        super(id, dtype, name, surname, pesel, height, weight, email);
        this.employedFrom = employedFrom;
        this.position = position;
        this.salary = salary;
        this.numberOfPositions = numberOfPositions;
    }

    public EmployeeDto(Employee employee){
        super(employee);
        this.employedFrom = employee.getEmployedFrom();
        this.position = employee.getCurrentPosition();
        this.salary = employee.getCurrentSalary();
        this.numberOfPositions = (long) employee.getPositions().size();
    }

}
