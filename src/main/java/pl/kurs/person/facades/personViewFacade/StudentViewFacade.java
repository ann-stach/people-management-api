package pl.kurs.person.facades.personViewFacade;

import org.springframework.stereotype.Component;
import pl.kurs.person.model.dto.PersonDto;
import pl.kurs.person.model.dto.StudentDto;
import pl.kurs.person.model.views.PersonView;

@Component
public class StudentViewFacade implements PersonViewFacade {

    @Override
    public boolean supports(String dtype) {
        return "Student".equals(dtype);
    }

    @Override
    public PersonDto handle(PersonView personView) {
        StudentDto dto = new StudentDto();
        dto.setId(personView.getId());
        dto.setDtype(personView.getDtype());
        dto.setName(personView.getName());
        dto.setSurname(personView.getSurname());
        dto.setPesel(personView.getPesel());
        dto.setHeight(personView.getHeight());
        dto.setWeight(personView.getWeight());
        dto.setEmail(personView.getEmail());

        dto.setUniversity(personView.getAdditionalField("university", String.class));
        dto.setCourse(personView.getAdditionalField("course", String.class));

        Integer academicYear = personView.getAdditionalField("academicYear", Integer.class);
        dto.setAcademicYear(academicYear != null ? academicYear : 0);

        Integer scholarship = personView.getAdditionalField("scholarship", Integer.class);
        dto.setScholarship(scholarship != null ? scholarship : 0);

        return dto;
    }
}

