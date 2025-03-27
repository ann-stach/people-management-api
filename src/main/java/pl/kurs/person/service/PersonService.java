package pl.kurs.person.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.person.exception.PersonNotFound;
import pl.kurs.person.facades.personFacade.PersonFacade;
import pl.kurs.person.model.Person;
import pl.kurs.person.model.command.CreatePersonCommand;
import pl.kurs.person.model.command.EditPersonCommand;
import pl.kurs.person.model.dto.PersonDto;
import pl.kurs.person.model.query.FindPersonQuery;
import pl.kurs.person.model.views.PersonView;
import pl.kurs.person.facades.personViewFacade.PersonViewFacade;
import pl.kurs.person.repository.PersonRepository;
import pl.kurs.person.repository.PersonViewRepository;
import pl.kurs.person.specification.PersonViewSpecification;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonViewRepository personViewRepository;
    private final Map<String, PersonFacade> facades;
    private final List<PersonViewFacade> viewFacades;


    @Transactional(readOnly = true)
    public Page<PersonDto> findAll(FindPersonQuery query, Pageable pageable) {
        Page<PersonView> pageOfViews = personViewRepository.findAll(new PersonViewSpecification(query), pageable);

        return pageOfViews.map(view -> viewFacades.stream()
                .filter(viewFacade -> viewFacade.supports(view.getDtype()))
                .findFirst()
                .map(viewFacade -> viewFacade.handle(view))
                .orElseThrow(() -> new IllegalArgumentException("No view facade found for dtype: " + view.getDtype()))
        );
    }

    @Transactional
    public PersonDto createPerson(CreatePersonCommand command) {
        PersonFacade facade = facades.get(command.getClassType().toLowerCase() + "Facade");
        Person person = personRepository.saveAndFlush(facade.createPerson(command.getParameters()));
        return facade.toDto(person);
    }

    @Transactional
    public PersonDto editPerson(int personId, EditPersonCommand command) {
        PersonFacade facade = facades.get(command.getClassType().toLowerCase() + "Facade");
        Person person = personRepository.findById(personId).orElseThrow(PersonNotFound::new);
        if (!person.getClass().getSimpleName().equals(command.getClassType())) {
            throw new IllegalArgumentException("Wrong class type for ID " + personId + ".");
        }

        return facade.toDto(personRepository.saveAndFlush(facade.editPerson(person, command.getParameters(), command.getVersion())));
    }


    @Transactional(readOnly = true)
    public PersonView findByIdView(int personId) {
        return personViewRepository.findById(personId).orElseThrow(PersonNotFound::new);
    }


}


