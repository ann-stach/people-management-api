package pl.kurs.person.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Student extends Person{

    @Size(min = 3, max =  250)
    private String university;
    @Min(1)
    @Max(5)
    private int academicYear;
    private String course;
    @PositiveOrZero
    private int scholarship;

    public Student(String name, String surname, String pesel, int height, int weight, String email, long version, String university, int academicYear, String course, int scholarship) {
        super(name, surname, pesel, height, weight, email, version);
        this.university = university;
        this.academicYear = academicYear;
        this.course = course;
        this.scholarship = scholarship;
    }

    public Student(Student student){
        super(student);
        this.university = student.getUniversity();
        this.academicYear = student.academicYear;
        this.course = student.getCourse();
        this.scholarship = student.getScholarship();
    }
}
