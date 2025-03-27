package pl.kurs.person.facades.personViewFacade;

import org.springframework.stereotype.Component;
import pl.kurs.person.model.dto.EmployeeDto;
import pl.kurs.person.model.dto.PersonDto;
import pl.kurs.person.model.views.PersonView;

import java.time.LocalDate;

@Component
public class EmployeeViewFacade implements PersonViewFacade {

    @Override
    public boolean supports(String dtype) {
        return "Employee".equals(dtype);
    }

    @Override
    public PersonDto handle(PersonView personView) {
        EmployeeDto dto = new EmployeeDto();
        dto.setId(personView.getId());
        dto.setDtype(personView.getDtype());
        dto.setName(personView.getName());
        dto.setSurname(personView.getSurname());
        dto.setPesel(personView.getPesel());
        dto.setHeight(personView.getHeight());
        dto.setWeight(personView.getWeight());
        dto.setEmail(personView.getEmail());

        dto.setEmployedFrom(personView.getAdditionalField("employedFrom", LocalDate.class));
        dto.setPosition(personView.getAdditionalField("currentPosition", String.class));

        Integer salary = personView.getAdditionalField("currentSalary", Integer.class);
        dto.setSalary(salary != null ? salary : 0);

        Integer numberOfPositions = personView.getAdditionalField("numberOfPositions", Integer.class);
        dto.setNumberOfPositions(numberOfPositions != null ? numberOfPositions.longValue() : 0);

        return dto;
    }
}