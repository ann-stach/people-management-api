package pl.kurs.person.model.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName("student")
public class StudentDto extends PersonDto{

    private String university;
    private int academicYear;
    private String course;
    private int scholarship;

    public StudentDto(int id, String dtype, String name, String surname, String pesel, int height, int weight, String email, String university, int academicYear, String course, int scholarship) {
        super(id, dtype, name, surname, pesel, height, weight, email);
        this.university = university;
        this.academicYear = academicYear;
        this.course = course;
        this.scholarship = scholarship;
    }
}
