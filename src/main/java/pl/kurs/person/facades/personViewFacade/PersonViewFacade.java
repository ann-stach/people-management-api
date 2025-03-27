package pl.kurs.person.facades.personViewFacade;

import pl.kurs.person.model.dto.PersonDto;
import pl.kurs.person.model.views.PersonView;

public interface PersonViewFacade {
    boolean supports(String dtype);
    PersonDto handle(PersonView personView);
}
