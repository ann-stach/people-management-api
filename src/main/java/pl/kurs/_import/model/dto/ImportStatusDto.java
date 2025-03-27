package pl.kurs._import.model.dto;

import pl.kurs._import.model.ImportStatus;

import java.time.LocalDateTime;

public record ImportStatusDto(long id, LocalDateTime submitDate, LocalDateTime startDate, long processedRows, ImportStatus.Status status) {

    public static ImportStatusDto toDto(ImportStatus importStatus) {
        return new ImportStatusDto(importStatus.getId(), importStatus.getSubmitDate(),
                importStatus.getStartDate(), importStatus.getProcessed(),
                importStatus.getStatus());
    }
}