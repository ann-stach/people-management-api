package pl.kurs.person.model.views;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

@Entity
@Getter
@Immutable
@NoArgsConstructor
@DiscriminatorValue("Employee")
public class EmployeeView extends PersonView{

    private LocalDate employedFrom;
    private String currentPosition;
    private Integer currentSalary;
    private int numberOfPositions;

    @Override
    protected void populateAdditionalFields() {
        getAdditionalFields().put("employedFrom", this.employedFrom);
        getAdditionalFields().put("currentPosition", this.currentPosition);
        getAdditionalFields().put("currentSalary", this.currentSalary);
        getAdditionalFields().put("numberOfPositions", this.numberOfPositions);
    }
}
