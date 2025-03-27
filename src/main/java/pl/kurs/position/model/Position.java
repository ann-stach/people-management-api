package pl.kurs.position.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.kurs.person.model.Employee;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Slf4j
@EqualsAndHashCode
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Size(min = 3, max = 50)
    private String name;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    @Positive
    private int salary;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public Position(String name, LocalDate dateFrom, LocalDate dateTo, int salary) {
        validateDates(dateFrom, dateTo);
        this.name = name;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.salary = salary;
    }

    public void hireEmployee(Employee employee) {
        validateHireEmployee(employee);

        employee.getPositions().add(this);
        this.setEmployee(employee);
        //jesli praca jest aktualna (endDate = null)
        if (this.dateTo == null) {
            log.info("Position is active, setting employee fields...");
            employee.setEmployedFrom(this.dateFrom);
            employee.setCurrentPosition(this.name);
            employee.setCurrentSalary(this.salary);
        } else {
            log.info("Position has ended, not updating employee fields.");
        }
    }

    public void dismissEmployee(Employee employee) {
        this.setDateTo(LocalDate.now());
        employee.setCurrentPosition("NOT_EMPLOYED");
        employee.setCurrentSalary(0);
        employee.setEmployedFrom(null);
    }
    private void validateHireEmployee(Employee employee) {
        for (Position position : employee.getPositions()) {
            if (datesOverlap(this.dateFrom, this.dateTo, position.getDateFrom(), position.getDateTo())) {
                throw new IllegalArgumentException("The hire period overlaps with another position for this employee.");
            }
        }
    }

    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        if (end1 == null) {
            end1 = LocalDate.MAX;
        }
        if (end2 == null) {
            end2 = LocalDate.MAX;
        }
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    private void validateDates(LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom == null) {
            throw new IllegalArgumentException("dateFrom cannot be null");
        }
        if (dateTo != null) {
            if (dateTo.isBefore(dateFrom)) {
                throw new IllegalArgumentException("DATE_FROM_MUST_BE_BEFORE_DATE_TO");
            }
        }
    }

}
