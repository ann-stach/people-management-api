package pl.kurs.person.facades.personFacade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.kurs.person.model.Student;
import pl.kurs.person.model.dto.StudentDto;

import java.util.Map;

@Component("studentFacade")
@RequiredArgsConstructor
public class StudentFacade implements PersonFacade<Student, StudentDto> {

    private static final String INSERT_STUDENT_SQL = "INSERT INTO person " +
            "(id, dtype, name, surname, pesel, height, weight, email, university, academic_year, course, scholarship, version) " +
            "VALUES (NEXT VALUE FOR person_seq, 'Student', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";

    @Override
    public Student createPersonInternal(Map<String, String> parameters) {
        Student student = new Student();
        student.setName(parameters.get("name"));
        student.setSurname(parameters.get("surname"));
        student.setPesel(parameters.get("pesel"));
        student.setHeight(Integer.parseInt(parameters.get("height")));
        student.setWeight(Integer.parseInt(parameters.get("weight")));
        student.setEmail(parameters.get("email"));
        student.setUniversity(parameters.get("university"));
        student.setAcademicYear(Integer.parseInt(parameters.get("academicYear")));
        student.setCourse(parameters.get("course"));
        student.setScholarship(Integer.parseInt(parameters.get("scholarship")));
        return student;
    }

    @Override
    public StudentDto toDto(Student student) {
        return new StudentDto(
                student.getId(),
                student.getDtype(),
                student.getName(),
                student.getSurname(),
                student.getPesel(),
                student.getHeight(),
                student.getWeight(),
                student.getEmail(),
                student.getUniversity(),
                student.getAcademicYear(),
                student.getCourse(),
                student.getScholarship());
    }


    @Override
    public Student editPersonInternal(Student student, Map<String, String> parameters, long version) {
        Student copy = clone(student);

        updateField(parameters, "name", copy::setName);
        updateField(parameters, "surname", copy::setSurname);
        updateField(parameters, "pesel", copy::setPesel);
        updateField(parameters, "height", height -> copy.setHeight(Integer.parseInt(height)));
        updateField(parameters, "weight", weight -> copy.setWeight(Integer.parseInt(weight)));
        updateField(parameters, "email", copy::setEmail);
        updateField(parameters, "university", copy::setUniversity);
        updateField(parameters, "academicYear", academicYear -> copy.setAcademicYear(Integer.parseInt(academicYear)));
        updateField(parameters, "course", copy::setCourse);
        updateField(parameters, "scholarship", scholarship -> copy.setScholarship(Integer.parseInt(scholarship)));
        copy.setVersion(version);

        return copy;
    }

    @Override
    public Student clone(Student student) {
        return new Student(student);
    }

    @Override
    public String[] getSqlArgs(String[] args) {
        return new String[] {args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10]};
    }

    @Override
    public String getSql() {
        return INSERT_STUDENT_SQL;
    }


}
