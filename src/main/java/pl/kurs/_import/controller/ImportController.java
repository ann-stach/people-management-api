package pl.kurs._import.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.kurs._import.model.ImportStatus;
import pl.kurs._import.model.dto.ImportStatusDto;
import pl.kurs._import.service.ImportService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/import")
@RequiredArgsConstructor
@Slf4j
public class ImportController {

    private final ImportService importService;

    @PostMapping
    public ResponseEntity<ImportStatusDto> importPeople(@RequestPart("people") MultipartFile file) throws IOException {
        ImportStatus actualImport = importService.startImport();
        importService.importFromCsvFile(file.getInputStream(), actualImport.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ImportStatusDto.toDto(actualImport));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImportStatusDto> getImportStatus(@PathVariable int id) {
        ImportStatus actualImport = importService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(ImportStatusDto.toDto(actualImport));
    }

}
