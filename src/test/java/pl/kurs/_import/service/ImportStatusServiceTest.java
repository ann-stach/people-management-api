package pl.kurs._import.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs._import.exception.StatusNotFound;
import pl.kurs._import.model.ImportStatus;
import pl.kurs._import.repository.ImportStatusRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportStatusServiceTest {

    @Mock
    private ImportStatusRepository importStatusRepository;

    @InjectMocks
    private ImportStatusService importStatusService;

    private int id;
    private ImportStatus importStatus;

    @BeforeEach
    void setUp() {
        id = 1;
        importStatus = new ImportStatus();
    }

    @Test
    void shouldUpdateImportStatusToProcessingAndCallMethodsAsExpected() {
        when(importStatusRepository.findById(id)).thenReturn(Optional.of(importStatus));

        importStatusService.updateToProcessing(id);

        verify(importStatusRepository, times(1)).findById(id);
        assertNotNull(importStatus.getStartDate());
        assertEquals(ImportStatus.Status.PROCESSING, importStatus.getStatus());
        verify(importStatusRepository, times(1)).saveAndFlush(importStatus);
    }

    @Test
    void shouldThrowStatusNotFoundWhenUpdatingToProcessingNonexistentStatus() {
        when(importStatusRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(StatusNotFound.class, () -> importStatusService.updateToProcessing(id));
        verify(importStatusRepository, never()).saveAndFlush(any());
    }

    @Test
    void shouldThrowStatusNotFoundWhenUpdatingProgressNonexistentStatus() {
        int progress = 50;
        when(importStatusRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(StatusNotFound.class, () -> importStatusService.updateProgress(id, progress));
        verify(importStatusRepository, never()).saveAndFlush(any());
    }

    @Test
    void shouldUpdateToSuccessWithCorrectNumberOfProcessedObjects() {
        int processed = 100;

        when(importStatusRepository.findById(id)).thenReturn(Optional.of(importStatus));

        importStatusService.updateToSuccess(id, processed);

        verify(importStatusRepository, times(1)).findById(id);
        assertEquals(processed, importStatus.getProcessed());
        assertEquals(ImportStatus.Status.SUCCESS, importStatus.getStatus());
        verify(importStatusRepository, times(1)).saveAndFlush(importStatus);
    }

    @Test
    void shouldThrowStatusNotFoundWhenUpdatingToSuccess() {
        int processed = 100;
        when(importStatusRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(StatusNotFound.class, () -> importStatusService.updateToSuccess(id, processed));
        verify(importStatusRepository, never()).saveAndFlush(any());
    }

    @Test
    void shouldUpdateToFailed() {
        int processed = 50;

        when(importStatusRepository.findById(id)).thenReturn(Optional.of(importStatus));

        importStatusService.updateToFailed(id, processed);

        verify(importStatusRepository, times(1)).findById(id);
        assertEquals(processed, importStatus.getProcessed());
        assertEquals(ImportStatus.Status.FAILED, importStatus.getStatus());
        verify(importStatusRepository, times(1)).saveAndFlush(importStatus);
    }

    @Test
    void shouldThrowStatusNotFoundWhenUpdatingToFailed() {
        int processed = 50;
        when(importStatusRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(StatusNotFound.class, () -> importStatusService.updateToFailed(id, processed));
        verify(importStatusRepository, never()).saveAndFlush(any());
    }
}