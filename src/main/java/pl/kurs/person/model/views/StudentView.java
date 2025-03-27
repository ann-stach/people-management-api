package pl.kurs.person.model.views;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Getter
@Immutable
@NoArgsConstructor
@DiscriminatorValue("Student")
public class StudentView extends PersonView {

    private String university;
    private Integer academicYear;
    private String course;
    private Integer scholarship;

    @Override
    protected void populateAdditionalFields() {
        getAdditionalFields().put("university", this.university);
        getAdditionalFields().put("academicYear", this.academicYear);
        getAdditionalFields().put("course", this.course);
        getAdditionalFields().put("scholarship", this.scholarship);
    }


}
