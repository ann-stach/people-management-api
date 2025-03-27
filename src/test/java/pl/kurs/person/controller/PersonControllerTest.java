package pl.kurs.person.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.kurs.Main;
import pl.kurs.PaginatedResponse;
import pl.kurs.person.model.PersonParameter;
import pl.kurs.person.model.command.CreatePersonCommand;
import pl.kurs.person.model.command.EditPersonCommand;
import pl.kurs.person.model.dto.EmployeeDto;
import pl.kurs.person.model.dto.PersonDto;
import pl.kurs.person.model.dto.RetireeDto;
import pl.kurs.person.model.dto.StudentDto;
import pl.kurs.person.model.views.EmployeeView;
import pl.kurs.person.model.views.RetireeView;
import pl.kurs.person.model.views.StudentView;
import pl.kurs.person.repository.PersonRepository;
import pl.kurs.person.service.PersonService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PersonControllerTest {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonService personService;

    @SpyBean
    private PersonRepository personRepository;

    private final CreatePersonCommand createEmployeeCommand = new CreatePersonCommand();
    private final CreatePersonCommand createRetireeCommand = new CreatePersonCommand();
    private final CreatePersonCommand createStudentCommand = new CreatePersonCommand();

    //do tworzenia peselów
    private static final Random RANDOM = new Random();

    @BeforeEach
    void setUp() {
        createEmployeeCommand.setClassType("Employee");
        List<PersonParameter> employeeParameters = new ArrayList<>();
        employeeParameters.add(new PersonParameter("name", "John"));
        employeeParameters.add(new PersonParameter("surname", "Doe"));
        employeeParameters.add(new PersonParameter("pesel", randomPesel()));
        employeeParameters.add(new PersonParameter("height", "180"));
        employeeParameters.add(new PersonParameter("weight", "75"));
        employeeParameters.add(new PersonParameter("email", "john.doe@example.com"));
        createEmployeeCommand.setParameters(employeeParameters);

        createStudentCommand.setClassType("Student");
        List<PersonParameter> studentParameters = new ArrayList<>();
        studentParameters.add(new PersonParameter("name", "John"));
        studentParameters.add(new PersonParameter("surname", "Doe"));
        studentParameters.add(new PersonParameter("pesel", randomPesel()));
        studentParameters.add(new PersonParameter("height", "180"));
        studentParameters.add(new PersonParameter("weight", "75"));
        studentParameters.add(new PersonParameter("email", "john.doe@example.com"));
        studentParameters.add(new PersonParameter("university", "Harvard University"));
        studentParameters.add(new PersonParameter("course", "Computer Science"));
        studentParameters.add(new PersonParameter("academicYear", "3"));
        studentParameters.add(new PersonParameter("scholarship", "2000"));
        createStudentCommand.setParameters(studentParameters);

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
    }

    @Test
    void shouldReturnTheSameSetOfPeopleAsExpected() throws Exception {
        personRepository.deleteAll();
        String employee = postman.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEmployeeCommand))
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String retiree = postman.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRetireeCommand))
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String student = postman.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createStudentCommand))
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EmployeeDto savedEmployee = objectMapper.readValue(employee, EmployeeDto.class);
        RetireeDto savedRetiree = objectMapper.readValue(retiree, RetireeDto.class);
        StudentDto savedStudent = objectMapper.readValue(student, StudentDto.class);

        String contentAsString = postman.perform(post("/api/v1/people/search"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PaginatedResponse<PersonDto> paginatedResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        List<PersonDto> people = paginatedResponse.getContent();

        Assertions.assertEquals(people.size(), 3);
        Assertions.assertTrue(people.contains(savedEmployee));
        Assertions.assertTrue(people.contains(savedRetiree));
        Assertions.assertTrue(people.contains(savedStudent));
    }

    @Test
    void shouldAddNewEmployeeWithCorrectParametersByAdmin() throws Exception {
        String responseJson = postman.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEmployeeCommand))
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.height").value(180))
                .andExpect(jsonPath("$.weight").value(75))
                .andExpect(jsonPath("$.position").value("NOT_EMPLOYED"))
                .andExpect(jsonPath("$.salary").value(0))
                .andExpect(jsonPath("$.employedFrom").isEmpty())
                .andExpect(jsonPath("$.numberOfPositions").value(0))
                .andReturn()
                .getResponse()
                .getContentAsString();

        EmployeeDto savedEmployee = objectMapper.readValue(responseJson, EmployeeDto.class);
        EmployeeView recentlyAdded = (EmployeeView) personService.findByIdView(savedEmployee.getId());

        Assertions.assertEquals("John", recentlyAdded.getName());
        Assertions.assertEquals("Doe", recentlyAdded.getSurname());
        Assertions.assertEquals("john.doe@example.com", recentlyAdded.getEmail());
        Assertions.assertEquals(savedEmployee.getPesel(), recentlyAdded.getPesel());
        Assertions.assertEquals(180, recentlyAdded.getHeight());
        Assertions.assertEquals(75, recentlyAdded.getWeight());
        Assertions.assertEquals("NOT_EMPLOYED", recentlyAdded.getCurrentPosition());
        Assertions.assertEquals(0, recentlyAdded.getCurrentSalary());
        Assertions.assertNull(recentlyAdded.getEmployedFrom());
    }

    @Test
    void shouldNotAddNewPersonByUserAndReturnStatus403Forbidden() throws Exception {
        postman.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEmployeeCommand))
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotAddNewPersonByImporterAndReturnStatus403Forbidden() throws Exception {
        postman.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEmployeeCommand))
                        .with(user("user").roles("IMPORTER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotAddNewPersonByEmployeeAndReturnStatus403Forbidden() throws Exception {
        postman.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEmployeeCommand))
                        .with(user("user").roles("EMPLOYEE")))
                .andExpect(status().isForbidden());
    }


    @Test
    void shouldNotAddNewPersonWithNoAuthAndReturnStatus401Unauthorized() throws Exception {
        postman.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEmployeeCommand)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAddNewStudentWithCorrectParametersByAdmin() throws Exception {
        String responseJson = postman.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createStudentCommand))
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.height").value(180))
                .andExpect(jsonPath("$.weight").value(75))
                .andExpect(jsonPath("$.university").value("Harvard University"))
                .andExpect(jsonPath("$.course").value("Computer Science"))
                .andExpect(jsonPath("$.academicYear").value(3))
                .andExpect(jsonPath("$.scholarship").value(2000))
                .andReturn()
                .getResponse()
                .getContentAsString();

        StudentDto savedStudent = objectMapper.readValue(responseJson, StudentDto.class);
        StudentView recentlyAdded = (StudentView) personService.findByIdView(savedStudent.getId());

        Assertions.assertEquals("John", recentlyAdded.getName());
        Assertions.assertEquals("Doe", recentlyAdded.getSurname());
        Assertions.assertEquals("john.doe@example.com", recentlyAdded.getEmail());
        Assertions.assertEquals(savedStudent.getPesel(), recentlyAdded.getPesel());
        Assertions.assertEquals(180, recentlyAdded.getHeight());
        Assertions.assertEquals(75, recentlyAdded.getWeight());
        Assertions.assertEquals("Harvard University", recentlyAdded.getUniversity());
        Assertions.assertEquals("Computer Science", recentlyAdded.getCourse());
        Assertions.assertEquals(3, recentlyAdded.getAcademicYear());
        Assertions.assertEquals(2000, recentlyAdded.getScholarship());
    }

    @Test
    void shouldAddNewRetireeWithCorrectParametersByAdmin() throws Exception {
        String responseJson = postman.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRetireeCommand))
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.height").value(180))
                .andExpect(jsonPath("$.weight").value(75))
                .andExpect(jsonPath("$.pension").value(3000))
                .andExpect(jsonPath("$.yearsWorked").value(40))
                .andReturn()
                .getResponse()
                .getContentAsString();

        RetireeDto savedRetiree = objectMapper.readValue(responseJson, RetireeDto.class);
        RetireeView recentlyAdded = (RetireeView) personService.findByIdView(savedRetiree.getId());

        Assertions.assertEquals("John", recentlyAdded.getName());
        Assertions.assertEquals("Doe", recentlyAdded.getSurname());
        Assertions.assertEquals("john.doe@example.com", recentlyAdded.getEmail());
        Assertions.assertEquals(savedRetiree.getPesel(), recentlyAdded.getPesel());
        Assertions.assertEquals(180, recentlyAdded.getHeight());
        Assertions.assertEquals(75, recentlyAdded.getWeight());
        Assertions.assertEquals(40, recentlyAdded.getYearsWorked());
        Assertions.assertEquals(3000, recentlyAdded.getPension());

    }

    @Test
    void shouldReturnStatusConflictWhenCreatingPersonWithPeselAlreadyAssignedToAnotherPerson() throws Exception {
        postman.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRetireeCommand))
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isCreated());

        postman.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRetireeCommand))
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldEditPersonFieldsWithSuccessByAdminAndChangeVersion() throws Exception {
        int id = personService.createPerson(createRetireeCommand).getId();

        EditPersonCommand editPersonCommand = new EditPersonCommand();
        editPersonCommand.setVersion(0L);
        editPersonCommand.setClassType("Retiree");
        List<PersonParameter> parametersEdit = new ArrayList<>();
        parametersEdit.add(new PersonParameter("name", "Jane"));
        parametersEdit.add(new PersonParameter("surname", "Smith"));
        editPersonCommand.setParameters(parametersEdit);

        postman.perform(patch("/api/v1/people/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editPersonCommand))
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Jane"))
                .andExpect(jsonPath("$.surname").value("Smith"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.height").value(180))
                .andExpect(jsonPath("$.weight").value(75))
                .andExpect(jsonPath("$.pension").value(3000))
                .andExpect(jsonPath("$.yearsWorked").value(40))
                .andReturn()
                .getResponse()
                .getContentAsString();

        RetireeView edited = (RetireeView) personService.findByIdView(id);

        Assertions.assertEquals("Jane", edited.getName());
        Assertions.assertEquals("Smith", edited.getSurname());
        Assertions.assertEquals("john.doe@example.com", edited.getEmail());
        Assertions.assertEquals(180, edited.getHeight());
        Assertions.assertEquals(75, edited.getWeight());
        Assertions.assertEquals(40, edited.getYearsWorked());
        Assertions.assertEquals(3000, edited.getPension());
        Assertions.assertEquals(1L, edited.getVersion());
    }

    @Test
    void shouldNotEditPersonByUserAndReturnStatus403Forbidden() throws Exception {
        int id = personService.createPerson(createRetireeCommand).getId();

        EditPersonCommand editPersonCommand = new EditPersonCommand();

        postman.perform(patch("/api/v1/people/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editPersonCommand))
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotEditPersonByEmployeeAndReturnStatus403Forbidden() throws Exception {
        int id = personService.createPerson(createRetireeCommand).getId();

        EditPersonCommand editPersonCommand = new EditPersonCommand();

        postman.perform(patch("/api/v1/people/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editPersonCommand))
                        .with(user("user").roles("EMPLOYEE")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotEditPersonByImporterAndReturnStatus403Forbidden() throws Exception {
        int id = personService.createPerson(createRetireeCommand).getId();

        EditPersonCommand editPersonCommand = new EditPersonCommand();

        postman.perform(patch("/api/v1/people/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editPersonCommand))
                        .with(user("user").roles("IMPORTER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotEditPersonWithNoAuthAndReturnStatus401Unauthorized() throws Exception {
        int id = personService.createPerson(createRetireeCommand).getId();

        EditPersonCommand editPersonCommand = new EditPersonCommand();

        postman.perform(patch("/api/v1/people/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editPersonCommand)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void shouldReturnStatusConflictErrorWhenWrongVersionInCommandIsGiven() throws Exception {
        int id = personService.createPerson(createRetireeCommand).getId();

        EditPersonCommand editPersonCommand = new EditPersonCommand();
        editPersonCommand.setVersion(-1L);
        editPersonCommand.setClassType("Retiree");
        List<PersonParameter> parametersEdit = new ArrayList<>();
        parametersEdit.add(new PersonParameter("name", "Jane"));
        parametersEdit.add(new PersonParameter("surname", "Smith"));
        editPersonCommand.setParameters(parametersEdit);


        postman.perform(patch("/api/v1/people/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editPersonCommand))
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isConflict());
    }


    @Test
    void shouldReturnStatusBadRequestWhenTypeInCommandInconsistentWithTypeForPersonOfGivenId() throws Exception {
        int id = personService.createPerson(createRetireeCommand).getId();

        EditPersonCommand editPersonCommand = new EditPersonCommand();
        editPersonCommand.setVersion(0L);
        editPersonCommand.setClassType("Employee");
        List<PersonParameter> parametersEdit = new ArrayList<>();
        parametersEdit.add(new PersonParameter("name", "Jane"));
        parametersEdit.add(new PersonParameter("surname", "Smith"));
        editPersonCommand.setParameters(parametersEdit);


        postman.perform(patch("/api/v1/people/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editPersonCommand))
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnStatusNotFoundWhenEditingPersonWithNonexistentId() throws Exception {
        int nonexistentId = 999;
        EditPersonCommand editPersonCommand = new EditPersonCommand();
        editPersonCommand.setVersion(0L);
        editPersonCommand.setClassType("Employee");
        postman.perform(patch("/api/v1/people/" + nonexistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editPersonCommand))
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
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



