package pl.kurs.person.facades.personFacade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.kurs.person.model.Employee;
import pl.kurs.person.model.dto.EmployeeDto;

import java.time.LocalDate;
import java.util.Map;

@Component("employeeFacade")
@RequiredArgsConstructor
public class EmployeeFacade implements PersonFacade<Employee, EmployeeDto> {

    public static final String INSERT_EMPLOYEE_SQL = "INSERT INTO person " +
            "(id, dtype, name, surname, pesel, height, weight, email, current_position, current_salary, version) " +
            "VALUES (NEXT VALUE FOR person_seq, 'Employee', ?, ?, ?, ?, ?, ?, 'NOT_EMPLOYED', 0, 0)";

    @Override
    public Employee createPersonInternal(Map<String, String> parameters) {
        Employee employee = new Employee();
        employee.setName(parameters.get("name"));
        employee.setSurname(parameters.get("surname"));
        employee.setPesel(parameters.get("pesel"));
        employee.setHeight(Integer.parseInt(parameters.get("height")));
        employee.setWeight(Integer.parseInt(parameters.get("weight")));
        employee.setEmail(parameters.get("email"));
        employee.setCurrentPosition("NOT_EMPLOYED");
        employee.setCurrentSalary(0);
        return employee;
    }

    @Override
    public EmployeeDto toDto(Employee employee) {

        return new EmployeeDto(
                employee.getId(),
                employee.getDtype(),
                employee.getName(),
                employee.getSurname(),
                employee.getPesel(),
                employee.getHeight(),
                employee.getWeight(),
                employee.getEmail(),
                employee.getEmployedFrom(),
                employee.getCurrentPosition(),
                employee.getCurrentSalary(),
                (long) employee.getPositions().size());
    }


    @Override
    public Employee editPersonInternal(Employee employee, Map<String, String> parameters, long version) {
        Employee copy =  clone(employee);

        updateField(parameters, "name", copy::setName);
        updateField(parameters, "surname", copy::setSurname);
        updateField(parameters, "pesel", copy::setPesel);
        updateField(parameters, "height", height -> copy.setHeight(Integer.parseInt(height)));
        updateField(parameters, "weight", weight -> copy.setWeight(Integer.parseInt(weight)));
        updateField(parameters, "email", copy::setEmail);
        updateField(parameters, "employedFrom", date -> copy.setEmployedFrom(LocalDate.parse(date)));
        updateField(parameters, "position", copy::setCurrentPosition);
        updateField(parameters, "salary", salary -> copy.setCurrentSalary(Integer.parseInt(salary)));
        copy.setVersion(version);

        return copy;
    }

    @Override
    public Employee clone(Employee employee) {
        return new Employee(employee);
    }

    @Override
    public String[] getSqlArgs(String[] args) {
        return new String[] {args[1], args[2], args[3], args[4], args[5], args[6]};
    }

    @Override
    public String getSql() {
        return INSERT_EMPLOYEE_SQL;
    }

}
