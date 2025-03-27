package pl.kurs.position.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.kurs.Main;
import pl.kurs.PaginatedResponse;
import pl.kurs.person.model.Employee;
import pl.kurs.person.model.PersonParameter;
import pl.kurs.person.model.command.CreatePersonCommand;
import pl.kurs.person.model.views.EmployeeView;
import pl.kurs.position.model.command.CreatePositionCommand;
import pl.kurs.position.model.dto.PositionDto;
import pl.kurs.person.repository.PersonRepository;
import pl.kurs.person.service.PersonService;
import pl.kurs.position.service.PositionService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PositionControllerTest {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PositionService positionService;
    @Autowired
    private PersonService personService;

    @SpyBean
    private PersonRepository personRepository;

    private CreatePositionCommand commandPositionNow;
    private CreatePositionCommand commandPositionPast;
    private CreatePositionCommand commandPositionOverlappingWithNow;

    //do tworzenia peselów
    private static final Random RANDOM = new Random();

    @BeforeEach
    void setUp() {

        commandPositionNow = new CreatePositionCommand(
                "Software Developer",
                LocalDate.of(2024, 2, 1),
                null,
                12_000);

        commandPositionPast = new CreatePositionCommand(
                "Structural Engineer",
                LocalDate.of(2016, 6, 6),
                LocalDate.of(2023, 1, 31),
                8_000);

        commandPositionOverlappingWithNow = new CreatePositionCommand(
                "Waiter",
                LocalDate.of(2023, 12, 1),
                LocalDate.of(2024, 2, 1),
                3_000);
    }


    @Test
    void shouldHireEmployeeWithSuccessAndUpdateCorrectFieldsByAdmin() throws Exception {
        int id = positionService.save(commandPositionNow).getId();

        int employeeId = personService.createPerson(createEmployeeWithUniquePesel()).getId();

        postman.perform(patch("/api/v1/positions/" + id + "/employees/" + employeeId + "/hire")
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isOk());

        EmployeeView editedPerson = (EmployeeView) personService.findByIdView(employeeId);
        PositionDto updatedPosition = positionService.findById(id);

        Assertions.assertEquals("Software Developer", editedPerson.getCurrentPosition());
        Assertions.assertEquals(12000, editedPerson.getCurrentSalary());
        Assertions.assertEquals(LocalDate.of(2024, 2, 1), editedPerson.getEmployedFrom());

        Assertions.assertEquals(employeeId, updatedPosition.getEmployee_id());
    }

    @Test
    void shouldHireEmployeeWithSuccessByEmployee() throws Exception {
        int id = positionService.save(commandPositionNow).getId();

        int employeeId = personService.createPerson(createEmployeeWithUniquePesel()).getId();

        postman.perform(patch("/api/v1/positions/" + id + "/employees/" + employeeId + "/hire")
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAddWorkFromThePastAndUpdateCorrectFieldsByAdmin() throws Exception {
        int id = positionService.save(commandPositionPast).getId();

        int employeeId = personService.createPerson(createEmployeeWithUniquePesel()).getId();

        postman.perform(patch("/api/v1/positions/" + id + "/employees/" + employeeId + "/hire")
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isOk());

        EmployeeView editedPerson = (EmployeeView) personService.findByIdView(employeeId);
        PositionDto updatedPosition = positionService.findById(id);

        Assertions.assertEquals("NOT_EMPLOYED", editedPerson.getCurrentPosition());
        Assertions.assertEquals(0, editedPerson.getCurrentSalary());
        Assertions.assertNull(editedPerson.getEmployedFrom());

        Assertions.assertEquals(employeeId, updatedPosition.getEmployee_id());
    }

    @Test
    void shouldNotAddWorkWhenOverlappingDatesByAdmin() throws Exception {
        int idNow = positionService.save(commandPositionNow).getId();
        int idOverlapping = positionService.save(commandPositionOverlappingWithNow).getId();

        int employeeId = personService.createPerson(createEmployeeWithUniquePesel()).getId();

        postman.perform(patch("/api/v1/positions/" + idNow + "/employees/" + employeeId + "/hire")
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isOk());

        postman.perform(patch("/api/v1/positions/" + idOverlapping + "/employees/" + employeeId + "/hire")
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isBadRequest());

        EmployeeView editedPerson = (EmployeeView) personService.findByIdView(employeeId);
        PositionDto positionNow = positionService.findById(idNow);
        PositionDto positionOverlapping = positionService.findById(idOverlapping);

        Assertions.assertEquals("Software Developer", editedPerson.getCurrentPosition());
        Assertions.assertEquals(12000, editedPerson.getCurrentSalary());
        Assertions.assertEquals(LocalDate.of(2024, 2, 1), editedPerson.getEmployedFrom());

        Assertions.assertEquals(employeeId, positionNow.getEmployee_id());
        Assertions.assertNull(positionOverlapping.getEmployee_id());
    }

    @Test
    void shouldNotHireEmployeeByUserAndReturnStatus403Forbidden() throws Exception {
        int idNow = positionService.save(commandPositionNow).getId();

        int employeeId = personService.createPerson(createEmployeeWithUniquePesel()).getId();

        postman.perform(patch("/api/v1/positions/" + idNow + "/employees/" + employeeId + "/hire")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotHireEmployeeByImporterAndReturnStatus403Forbidden() throws Exception {
        int idNow = positionService.save(commandPositionNow).getId();

        int employeeId = personService.createPerson(createEmployeeWithUniquePesel()).getId();

        postman.perform(patch("/api/v1/positions/" + idNow + "/employees/" + employeeId + "/hire")
                        .with(user("user").roles("IMPORTER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotHireEmployeeWithNoAuthAndReturnStatus401Unauthorized() throws Exception {
        int idNow = positionService.save(commandPositionNow).getId();
        int employeeId = personService.createPerson(createEmployeeWithUniquePesel()).getId();

        postman.perform(patch("/api/v1/positions/" + idNow + "/employees/" + employeeId + "/hire"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldNotHireWhenIdDoesNotBelongToTypeEmployeeAndReturnStatusNotFound() throws Exception {
        int idNow = positionService.save(commandPositionNow).getId();

        CreatePersonCommand createRetireeCommand = new CreatePersonCommand();
        createRetireeCommand.setClassType("Retiree");
        List<PersonParameter> retireeParameters = new ArrayList<>();
        retireeParameters.add(new PersonParameter("name", "John"));
        retireeParameters.add(new PersonParameter("surname", "Doe"));
        retireeParameters.add(new PersonParameter("pesel", randomPesel()));
        retireeParameters.add(new PersonParameter("height", "180"));
        retireeParameters.add(new PersonParameter("weight", "75"));
        retireeParameters.add(new PersonParameter("email", "john.doe@example.com"));
        retireeParameters.add(new PersonParameter("yearsWorked", "40"));
        retireeParameters.add(new PersonParameter("pension", "3000"));
        createRetireeCommand.setParameters(retireeParameters);

        int retireeId = personService.createPerson(createRetireeCommand).getId();

        postman.perform(patch("/api/v1/positions/" + idNow + "/employees/" + retireeId + "/hire")
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnStatusNotFoundWhenHireOnNonexistentPosition() throws Exception {
        int idNonexistent = 999999;
        int employeeId = personService.createPerson(createEmployeeWithUniquePesel()).getId();

        postman.perform(patch("/api/v1/positions/" + idNonexistent + "/employees/" + employeeId + "/hire")
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnStatusNotFoundWhenHireNonexistentEmployee() throws Exception {
        int id = positionService.save(commandPositionNow).getId();
        int nonexistentEmployeeId = 999999;

        postman.perform(patch("/api/v1/positions/" + id + "/employees/" + nonexistentEmployeeId + "/hire")
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }


    //dismiss
    @Test
    void shouldDismissEmployeeWithSuccessAndUpdateCorrectFieldsByAdmin() throws Exception {
        int idNow = positionService.save(commandPositionNow).getId();
        int employeeId = personService.createPerson(createEmployeeWithUniquePesel()).getId();

        postman.perform(patch("/api/v1/positions/" + idNow + "/employees/" + employeeId + "/hire")
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isOk());

        postman.perform(patch("/api/v1/positions/" + idNow + "/employees/" + employeeId + "/dismiss")
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isOk());

        EmployeeView dismissedPerson = (EmployeeView) personService.findByIdView(employeeId);
        PositionDto positionNow = positionService.findById(idNow);


        Assertions.assertEquals("NOT_EMPLOYED", dismissedPerson.getCurrentPosition());
        Assertions.assertEquals(0, dismissedPerson.getCurrentSalary());
        Assertions.assertNull(dismissedPerson.getEmployedFrom());

        Assertions.assertEquals(employeeId, dismissedPerson.getId());
        Assertions.assertEquals(LocalDate.now(), positionNow.getDateTo());

    }

    @Test
    void shouldNotDismissEmployeeByUserAndReturnStatus403Forbidden() throws Exception {
        int idNow = positionService.save(commandPositionNow).getId();
        int employeeId = personService.createPerson(createEmployeeWithUniquePesel()).getId();

        postman.perform(patch("/api/v1/positions/" + idNow + "/employees/" + employeeId + "/dismiss")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotDismissEmployeeByImporterAndReturnStatus403Forbidden() throws Exception {
        int idNow = positionService.save(commandPositionNow).getId();
        int employeeId = personService.createPerson(createEmployeeWithUniquePesel()).getId();

        postman.perform(patch("/api/v1/positions/" + idNow + "/employees/" + employeeId + "/dismiss")
                        .with(user("user").roles("IMPORTER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotDismissEmployeeWithNoAuthAndReturnStatus401Unauthorized() throws Exception {
        int idNow = positionService.save(commandPositionNow).getId();
        int employeeId = personService.createPerson(createEmployeeWithUniquePesel()).getId();

        postman.perform(patch("/api/v1/positions/" + idNow + "/employees/" + employeeId + "/dismiss"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnStatusBadRequestWhenDismissFromPositionNotAssignedToAnyone() throws Exception {
        int idNow = positionService.save(commandPositionNow).getId();
        int employeeId = personService.createPerson(createEmployeeWithUniquePesel()).getId();

        postman.perform(patch("/api/v1/positions/" + idNow + "/employees/" + employeeId + "/dismiss")
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnStatusBadRequestWhenDismissFromPositionNotAssignedToEmployeeWhichIsGivenInPath() throws Exception {
        int idNow = positionService.save(commandPositionNow).getId();
        int employeeId = personService.createPerson(createEmployeeWithUniquePesel()).getId();
        int employeeNotAssigned = personService.createPerson(createEmployeeWithUniquePesel()).getId();

        postman.perform(patch("/api/v1/positions/" + idNow + "/employees/" + employeeId + "/hire")
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isOk());

        postman.perform(patch("/api/v1/positions/" + idNow + "/employees/" + employeeNotAssigned + "/dismiss")
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isBadRequest());

        EmployeeView employeeWitAssignedPosition = (EmployeeView) personService.findByIdView(employeeId);
        PositionDto positionNow = positionService.findById(idNow);


        Assertions.assertEquals(positionNow.getName(), employeeWitAssignedPosition.getCurrentPosition());
        Assertions.assertEquals(positionNow.getSalary(), employeeWitAssignedPosition.getCurrentSalary());
        Assertions.assertEquals(positionNow.getDateFrom(), employeeWitAssignedPosition.getEmployedFrom());
        Assertions.assertNull(positionNow.getDateTo());

    }

    @Test
    void shouldReturnCorrectSetOfPositionsForEmployee() throws Exception {
        int idNow = positionService.save(commandPositionNow).getId();
        int idPast = positionService.save(commandPositionPast).getId();
        int employeeId = personService.createPerson(createEmployeeWithUniquePesel()).getId();

        postman.perform(patch("/api/v1/positions/" + idNow + "/employees/" + employeeId + "/hire")
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isOk());
        postman.perform(patch("/api/v1/positions/" + idPast + "/employees/" + employeeId + "/hire")
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isOk());

        String contentAsString = postman.perform(get("/api/v1/positions/employees/" + employeeId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PaginatedResponse<PositionDto> paginatedResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        List<PositionDto> positions = paginatedResponse.getContent();

        PositionDto positionNow = positionService.findById(idNow);
        PositionDto positionPast = positionService.findById(idPast);


        Assertions.assertEquals(positions.size(), 2);
        Assertions.assertTrue(positions.contains(positionPast));
        Assertions.assertTrue(positions.contains(positionNow));
    }

    private CreatePersonCommand createEmployeeWithUniquePesel() {
        CreatePersonCommand createEmployeeCommand = new CreatePersonCommand();
        createEmployeeCommand.setClassType("Employee");
        List<PersonParameter> employeeParameters = new ArrayList<>();
        employeeParameters.add(new PersonParameter("name", "John"));
        employeeParameters.add(new PersonParameter("surname", "Doe"));
        employeeParameters.add(new PersonParameter("pesel", randomPesel()));
        employeeParameters.add(new PersonParameter("height", "180"));
        employeeParameters.add(new PersonParameter("weight", "75"));
        employeeParameters.add(new PersonParameter("email", "john.doe@example.com"));
        createEmployeeCommand.setParameters(employeeParameters);
        return createEmployeeCommand;
    }

    private static String randomPesel() {
        int[] weights = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};
        int[] peselDigits = new int[11];

        for (int i = 0; i < 10; i++) {
            peselDigits[i] = RANDOM.nextInt(10);
        }

        int checksum = 0;
        for (int i = 0; i < 10; i++) {
            checksum += peselDigits[i] * weights[i];
        }
        checksum = (10 - (checksum % 10)) % 10;
        peselDigits[10] = checksum;

        StringBuilder pesel = new StringBuilder();
        for (int digit : peselDigits) {
            pesel.append(digit);
        }
        return pesel.toString();
    }

}