package pl.kurs.person.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.kurs.person.exception.PersonNotFound;
import pl.kurs.person.facades.personFacade.EmployeeFacade;
import pl.kurs.person.facades.personFacade.PersonFacade;
import pl.kurs.person.model.Employee;
import pl.kurs.person.model.Person;
import pl.kurs.person.model.PersonParameter;
import pl.kurs.person.model.command.CreatePersonCommand;
import pl.kurs.person.model.command.EditPersonCommand;
import pl.kurs.person.model.dto.EmployeeDto;
import pl.kurs.person.model.dto.PersonDto;
import pl.kurs.person.model.query.FindPersonQuery;
import pl.kurs.person.model.views.PersonView;
import pl.kurs.person.facades.personViewFacade.PersonViewFacade;
import pl.kurs.person.repository.PersonRepository;
import pl.kurs.person.repository.PersonViewRepository;
import pl.kurs.person.specification.PersonViewSpecification;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonViewRepository personViewRepository;

    @InjectMocks
    private PersonService personService;

    @Mock
    private Map<String, PersonFacade> facades;

    @Mock
    private PersonViewFacade employeeViewFacade;


    private Person person;
    private PersonView personView;
    private EmployeeFacade employeeFacade;
    private EmployeeDto employeeDto;
    private Employee employee;

    @BeforeEach
    void setup() {
        personService = new PersonService(personRepository, personViewRepository, facades, List.of(employeeViewFacade));

        person = mock(Person.class);
        personView = mock(PersonView.class);
        employeeFacade = mock(EmployeeFacade.class);
        employeeDto = mock(EmployeeDto.class);
        employee = mock(Employee.class);

        when(facades.get("employeeFacade")).thenReturn(employeeFacade);
    }


    @Test
    void shouldReturnCorrectNumberOfPeopleAndCallRepositoryMethodsAsExpected() {
        Pageable pageable = PageRequest.of(0, 5);
        FindPersonQuery query = mock(FindPersonQuery.class);

        when(personViewRepository.findAll(any(PersonViewSpecification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(personView)));

        when(personView.getDtype()).thenReturn("Employee");

        when(employeeViewFacade.supports("Employee")).thenReturn(true);

        when(employeeViewFacade.handle(personView)).thenReturn(employeeDto);

        Page<PersonDto> result = personService.findAll(query, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(employeeDto, result.getContent().get(0));

        verify(personViewRepository, times(1))
                .findAll(any(PersonViewSpecification.class), eq(pageable));

        verify(employeeViewFacade, times(1)).supports("Employee");
        verify(employeeViewFacade, times(1)).handle(personView);

    }

    @Test
    void shouldReturnCorrectPersonAndCallRepositoryMethodsAsExpected() {
        int personId = 1;
        when(personViewRepository.findById(personId)).thenReturn(Optional.of(personView));

        PersonView result = personService.findByIdView(personId);

        Assertions.assertNotNull(result);
        verify(personViewRepository, times(1)).findById(personId);
    }

    @Test
    void shouldThrowPersonNotFoundExceptionWhenPersonNotExists() {
        int personId = 1;

        when(personViewRepository.findById(personId)).thenReturn(Optional.empty());

        Assertions.assertThrows(PersonNotFound.class, () -> personService.findByIdView(personId));

        verify(personViewRepository, times(1)).findById(personId);
    }


    @Test
    void shouldCreateNewPersonAndCallRepositoryMethodsAsExpected() {
        CreatePersonCommand createPersonCommand = mock(CreatePersonCommand.class);
        PersonParameter personParameter = mock(PersonParameter.class);

        when(createPersonCommand.getClassType()).thenReturn("Employee");
        when(createPersonCommand.getParameters()).thenReturn(List.of(personParameter));

        when(employeeFacade.createPerson(createPersonCommand.getParameters())).thenReturn(employee);

        when(personRepository.saveAndFlush(employee)).thenReturn(employee);
        when(employeeFacade.toDto(employee)).thenReturn(employeeDto);

        PersonDto result = personService.createPerson(createPersonCommand);

        Assertions.assertNotNull(result);
        verify(personRepository, times(1)).saveAndFlush(employee);
        verify(facades, times(1)).get("employeeFacade");
        verify(employeeFacade, times(1)).createPerson(createPersonCommand.getParameters());
        verify(employeeFacade, times(1)).toDto(employee);
    }

    @Test
    void shouldThrowPersonNotFoundExceptionWhenEditedPersonNotExists() {
        int personId = 1;
        EditPersonCommand command = mock(EditPersonCommand.class);
        when(command.getClassType()).thenReturn("Employee");

        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        Assertions.assertThrows(PersonNotFound.class, () -> personService.editPerson(personId, command));

        verify(personRepository, times(1)).findById(personId);
    }


    @Test
    void shouldSuccessfullyEditPerson() {
        int personId = 1;
        EditPersonCommand command = mock(EditPersonCommand.class);

        when(personRepository.findById(personId)).thenReturn(Optional.of(employee));
        when(command.getClassType()).thenReturn("Employee");
        when(employeeFacade.editPerson(employee, command.getParameters(), command.getVersion())).thenReturn(employee);
        when(employeeFacade.toDto(employee)).thenReturn(employeeDto);
        when(personRepository.saveAndFlush(employee)).thenReturn(employee);

        PersonDto result = personService.editPerson(personId, command);

        Assertions.assertNotNull(result);
        verify(personRepository, times(1)).findById(personId);
        verify(facades, times(1)).get("employeeFacade");
        verify(employeeFacade, times(1)).editPerson(employee, command.getParameters(), command.getVersion());
        verify(personRepository, times(1)).saveAndFlush(employee);
        verify(employeeFacade, times(1)).toDto(employee);
    }

}