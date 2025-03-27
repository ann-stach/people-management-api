package pl.kurs._import.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.kurs.Main;
import pl.kurs._import.model.ImportStatus;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ImportControllerTest {

    @Autowired
    private MockMvc postman;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldReturnStatusAcceptedWhenImportInitializedByAdmin() throws Exception {
        MockMultipartFile file = new MockMultipartFile("people", "test.csv", "text/csv", "sample data".getBytes());

        String contentAsString = postman.perform(multipart("/api/v1/import")
                        .file(file)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isAccepted())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ImportStatus importStatus = objectMapper.readValue(contentAsString, ImportStatus.class);
        Assertions.assertEquals(importStatus.getStatus(), ImportStatus.Status.NEW);
    }

    @Test
    void shouldReturnStatusAcceptedWhenImportInitializedByImporter() throws Exception {
        MockMultipartFile file = new MockMultipartFile("people", "test.csv", "text/csv", "sample data".getBytes());

        String contentAsString = postman.perform(multipart("/api/v1/import")
                        .file(file)
                        .with(user("user").roles("IMPORTER")))
                .andExpect(status().isAccepted())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ImportStatus importStatus = objectMapper.readValue(contentAsString, ImportStatus.class);
        Assertions.assertEquals(importStatus.getStatus(), ImportStatus.Status.NEW);
    }

    @Test
    void shouldReturnStatusIsForbiddenWhenImportInitializedByEmployee() throws Exception {
        MockMultipartFile file = new MockMultipartFile("people", "test.csv", "text/csv", "sample data".getBytes());

        postman.perform(multipart("/api/v1/import")
                        .file(file)
                        .with(user("user").roles("EMPLOYEE")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnStatusUnauthorizedWhenImportInitializedByNoAuth() throws Exception {
        MockMultipartFile file = new MockMultipartFile("people", "test.csv", "text/csv", "sample data".getBytes());

        postman.perform(multipart("/api/v1/import")
                        .file(file))
                .andExpect(status().isUnauthorized());
    }

}