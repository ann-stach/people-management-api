package pl.kurs.person.facades.personViewFacade;

import org.springframework.stereotype.Component;
import pl.kurs.person.model.dto.PersonDto;
import pl.kurs.person.model.dto.RetireeDto;
import pl.kurs.person.model.views.PersonView;

@Component
public class RetireeViewFacade implements PersonViewFacade {

    @Override
    public boolean supports(String dtype) {
        return "Retiree".equals(dtype);
    }

    @Override
    public PersonDto handle(PersonView personView) {
        RetireeDto dto = new RetireeDto();
        dto.setId(personView.getId());
        dto.setDtype(personView.getDtype());
        dto.setName(personView.getName());
        dto.setSurname(personView.getSurname());
        dto.setPesel(personView.getPesel());
        dto.setHeight(personView.getHeight());
        dto.setWeight(personView.getWeight());
        dto.setEmail(personView.getEmail());

        Integer pension = personView.getAdditionalField("pension", Integer.class);
        dto.setPension(pension != null ? pension : 0);

        Integer yearsWorked = personView.getAdditionalField("yearsWorked", Integer.class);
        dto.setYearsWorked(yearsWorked != null ? yearsWorked : 0);

        return dto;
    }
}