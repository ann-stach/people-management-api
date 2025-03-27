package pl.kurs._import.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs._import.exception.StatusNotFound;
import pl.kurs._import.model.ImportRequest;
import pl.kurs._import.model.ImportStatus;
import pl.kurs._import.repository.ImportStatusRepository;
import pl.kurs.person.facades.personFacade.PersonFacade;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportService {

    private static final String LOCK_KEY = "import-lock";
    private static final String QUEUE_KEY = "import_queue";

    private final Map<String, PersonFacade> facades;
    private final JdbcTemplate jdbcTemplate;
    private final ImportStatusRepository importStatusRepository;
    private final ImportStatusService importStatusService;
    private final LockService lockService;
    private final StringRedisTemplate redisTemplate;


    @Transactional
    @Async("personImportExecutor")
    public void importFromCsvFile(InputStream inputStream, int id) throws JsonProcessingException {

        boolean lockAcquired = false;

        try {
            lockAcquired = lockService.acquireLock(LOCK_KEY);

            if (!lockAcquired) {
                String info = "Maximum number of concurrent imports reached. Import in queue.";
                enqueueImport(new ImportRequest(id, inputStream));
                log.info(info);
                return;
            }

            importStatusService.updateToProcessing(id);
            AtomicInteger counter = new AtomicInteger(0);
            AtomicLong start = new AtomicLong(System.currentTimeMillis());
            long totalStartTime = System.currentTimeMillis();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                reader.lines()
                        .map(line -> line.split(","))
                        .peek(command -> countTime(counter, start, id))
                        .forEach(this::save);

                importStatusService.updateToSuccess(id, counter.get());
            } catch (Exception e) {
                String reason = "Error at line " + counter.get() + ": " + e.getMessage();
                log.error("Import failed at line {}", counter.get(), e);
                importStatusService.updateToFailed(id, counter.get());
                throw new RuntimeException("IMPORT FAILED || " + reason, e);
            }

            logTotalTime(totalStartTime);
        } finally {
            if (lockAcquired) {
                lockService.releaseLock(LOCK_KEY);
                processQueuedImport();
            }
        }
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public ImportStatus findById(int id) {
        return importStatusRepository.findById(id).orElseThrow(StatusNotFound::new);
    }

    public ImportStatus startImport() {
        return importStatusRepository.saveAndFlush(new ImportStatus());
    }


    private void save(String[] args) {

        try {
            PersonFacade facade = facades.get(args[0].toLowerCase().toLowerCase() + "Facade");
            if (facade == null) {
                throw new IllegalArgumentException();
            }
            jdbcTemplate.update(facade.getSql(), facade.getSqlArgs(args));

        } catch (Exception e) {
            String errorMessage = String.format("Error saving record: %s", String.join(", ", args));
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }


    private void countTime(AtomicInteger counter, AtomicLong start, int id) {
        int progress = counter.incrementAndGet();
        if (progress % 50_000 == 0) {
            log.info("Imported: {} in {} ms", counter, (System.currentTimeMillis() - start.get()));
            start.set(System.currentTimeMillis());
            importStatusService.updateProgress(id, progress);
        }
    }

    private void logTotalTime(long totalStartTime) {
        long totalTime = System.currentTimeMillis() - totalStartTime;
        log.info("Total time taken for import: {} ms", totalTime);
    }

    public void processQueuedImport() throws JsonProcessingException {
        String queuedImportJson = redisTemplate.opsForList().leftPop(QUEUE_KEY);
        if (queuedImportJson != null) {
            ImportRequest importRequest = new ObjectMapper().readValue(queuedImportJson, ImportRequest.class);
            importFromCsvFile(importRequest.getInputStream(), importRequest.getId());
        }
    }

    public void enqueueImport(ImportRequest request) {
        try {
            String jsonRequest = new ObjectMapper().writeValueAsString(request);
            redisTemplate.opsForList().rightPush(QUEUE_KEY, jsonRequest);
        } catch (JsonProcessingException e) {
            log.error("Failed to enqueue import request", e);
        }
    }


}
