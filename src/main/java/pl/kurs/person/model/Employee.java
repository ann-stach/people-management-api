package pl.kurs.person.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.position.model.Position;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = "positions")
public class Employee extends Person{

    private LocalDate employedFrom;
    private String currentPosition;
    private Integer currentSalary;

    @OneToMany(mappedBy = "employee", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<Position> positions = new HashSet<>();


    public Employee(String name, String surname, String pesel, int height, int weight, String email, long version) {
        super(name, surname, pesel, height, weight, email, version);
        this.currentPosition = "NOT_EMPLOYED";
    }

    public Employee(Employee employee) {
        super((employee));
        this.employedFrom = employee.getEmployedFrom();
        this.currentPosition = employee.getCurrentPosition();
        this.currentSalary = employee.getCurrentSalary();
        this.positions = employee.getPositions();
    }

}

