package pl.kurs._import.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LockService {

    private static final String ACQUIRE_LOCK_SQL = """
        UPDATE distributed_lock
        SET active_imports = active_imports + 1
        WHERE lock_key = ? AND active_imports < max_imports
    """;

    private static final String RELEASE_LOCK_SQL = """
        UPDATE distributed_lock
        SET active_imports = active_imports - 1
        WHERE lock_key = ? AND active_imports > 0
    """;

    private final JdbcTemplate jdbcTemplate;


    @Transactional
    public boolean acquireLock(String lockKey) {
        int rowsUpdated = jdbcTemplate.update(ACQUIRE_LOCK_SQL, lockKey);
        boolean acquired = rowsUpdated == 1;

        if (acquired) {
            log.info("Lock acquired for key: {}", lockKey);
        } else {
            log.warn("Failed to acquire lock for key: {}, maximum concurrent imports reached.", lockKey);
        }
        return acquired;
    }

    @Transactional
    public void releaseLock(String lockKey) {
        int rowsUpdated = jdbcTemplate.update(RELEASE_LOCK_SQL, lockKey);

        if (rowsUpdated == 1) {
            log.info("Lock released for key: {}", lockKey);
        } else {
            log.warn("Lock release attempt was either redundant or failed for key: {}", lockKey);
        }
    }

}