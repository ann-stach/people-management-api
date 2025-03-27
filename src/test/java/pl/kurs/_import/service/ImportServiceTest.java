//package pl.kurs._import.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.junit.jupiter.MockitoSettings;
//import org.mockito.quality.Strictness;
//import org.springframework.data.redis.core.ListOperations;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.PreparedStatementSetter;
//import pl.kurs._import.exception.StatusNotFound;
//import pl.kurs._import.model.ImportRequest;
//import pl.kurs._import.model.ImportStatus;
//import pl.kurs._import.repository.ImportStatusRepository;
//import pl.kurs.person.facades.personFacade.EmployeeFacade;
//import pl.kurs.person.facades.personFacade.PersonFacade;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.util.Map;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
//class ImportServiceTest {
//
//    @Mock
//    private ImportStatusRepository importStatusRepository;
//
//    @Mock
//    private ImportStatusService importStatusService;
//
//    @Mock
//    private JdbcTemplate jdbcTemplate;
//
//    @Mock
//    private Map<String, PersonFacade> facades;
//
//    @Mock
//    private LockService lockService;
//
//    @Mock
//    private StringRedisTemplate redisTemplate;
//
//    @Mock
//    private ListOperations<String, String> listOperations;
//
//    @InjectMocks
//    private ImportService importService;
//
//
//    @Test
//    void shouldStartImportAndCallMethodsAsExpected() {
//        ImportStatus expectedImportStatus = new ImportStatus();
//        when(importStatusRepository.saveAndFlush(any(ImportStatus.class))).thenReturn(expectedImportStatus);
//
//        ImportStatus actualImportStatus = importService.startImport();
//
//        assertNotNull(actualImportStatus);
//        assertSame(expectedImportStatus, actualImportStatus);
//
//        verify(importStatusRepository, times(1)).saveAndFlush(any(ImportStatus.class));
//    }
//
//    @Test
//    void shouldFindImportStatusByIdAndCallMethodsAsExpected() {
//        int id = 1;
//        ImportStatus importStatus = new ImportStatus();
//        when(importStatusRepository.findById(id)).thenReturn(Optional.of(importStatus));
//
//        ImportStatus foundImportStatus = importService.findById(id);
//
//        assertNotNull(foundImportStatus);
//        verify(importStatusRepository, times(1)).findById(id);
//    }
//
//    @Test
//    void shouldThrowStatusNotFoundWhenFindByIdFails() {
//        int id = 1;
//        when(importStatusRepository.findById(id)).thenReturn(Optional.empty());
//
//        assertThrows(StatusNotFound.class, () -> importService.findById(id));
//    }
//
//    @Test
//    void shouldImportFromCsvFileSuccessfully() throws JsonProcessingException {
//        int id = 1;
//        String lockKey = "import-lock";
//        String csvContent = "Employee,Michael,Davis,31541735982,170,113,joseph.moore@example.com,null,null,null,null,null,null,NOT_EMPLOYED,0,null,";
//        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
//
//        EmployeeFacade employeeFacade = mock(EmployeeFacade.class);
//        when(facades.get(anyString())).thenReturn(employeeFacade);
//        when(employeeFacade.getSql()).thenReturn("INSERT INTO person " +
//                "(id, dtype, name, surname, pesel, height, weight, email, current_position, current_salary, version) " +
//                "VALUES (NEXT VALUE FOR person_seq, 'Employee', ?, ?, ?, ?, ?, ?, 'NOT_EMPLOYED', 0, 0)");
//        when(employeeFacade.getSqlArgs(any())).thenReturn(new String[]{"Michael", "Davis", "31541735982", "170", "113", "joseph.moore@example.pl"});
//        when(lockService.acquireLock(lockKey)).thenReturn(true);
//        when(redisTemplate.opsForList()).thenReturn(listOperations);
//        when(listOperations.leftPop("import_queue")).thenReturn(null);
//
//        importService.importFromCsvFile(inputStream, id);
//
//        verify(lockService, times(1)).acquireLock(lockKey);
//        verify(importStatusService, times(1)).updateToProcessing(id);
//        verify(jdbcTemplate, times(1)).update(
//                eq("INSERT INTO person (id, dtype, name, surname, pesel, height, weight, email, current_position, current_salary, version) VALUES (NEXT VALUE FOR person_seq, 'Employee', ?, ?, ?, ?, ?, ?, 'NOT_EMPLOYED', 0, 0)"),
//                eq("Michael"),
//                eq("Davis"),
//                eq("31541735982"),
//                eq("170"),
//                eq("113"),
//                eq("joseph.moore@example.pl")
//        );
//        verify(importStatusService, times(1)).updateToSuccess(eq(id), eq(1));
//        verify(lockService, times(1)).releaseLock(lockKey);
//    }
//
//    @Test
//    void shouldFailToImportWhenLockCannotBeAcquired() throws JsonProcessingException {
//        int id = 1;
//        String lockKey = "import-lock";
//        String csvContent = "Employee,Michael,Davis,31541735982,170,113,joseph.moore@example.com,null,null,null,null,null,null,NOT_EMPLOYED,0,null,";
//        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
//
//        when(lockService.acquireLock(lockKey)).thenReturn(false);
//        when(redisTemplate.opsForList()).thenReturn(listOperations);
//
//        importService = spy(importService);
//
//        doNothing().when(importService).enqueueImport(any(ImportRequest.class));
//
//        importService.importFromCsvFile(inputStream, id);
//
//        verify(lockService, times(1)).acquireLock(lockKey);
//        verify(importStatusService, times(0)).updateToFailed(id, 0);
//        verify(lockService, times(0)).releaseLock(lockKey);
//
//        verify(importService, times(1)).enqueueImport(any(ImportRequest.class));
//    }
//
//    @Test
//    void shouldHandleCsvImportFailure() {
//        int id = 1;
//        String lockKey = "import-lock";
//        String csvContent = "invalid_command,arg1,arg2\n";
//        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
//
//        when(facades.get(anyString())).thenReturn(null);
//        when(lockService.acquireLock(lockKey)).thenReturn(true);
//
//        assertThrows(RuntimeException.class,
//                () -> importService.importFromCsvFile(inputStream, id));
//
//        verify(lockService, times(1)).acquireLock(lockKey);
//        verify(lockService, times(1)).releaseLock(lockKey);
//        verify(importStatusService, times(1)).updateToProcessing(id);
//        verify(importStatusService, times(1)).updateToFailed(eq(id), eq(1));
//        verify(jdbcTemplate, times(0)).update(anyString(), (PreparedStatementSetter) any());
//    }
//
//}