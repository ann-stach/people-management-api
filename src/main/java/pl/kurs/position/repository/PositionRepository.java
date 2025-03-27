package pl.kurs.position.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.position.model.Position;

import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Integer> {

    @Query("SELECT p FROM Position p JOIN FETCH p.employee WHERE p.employee.id = ?1")
    Page<Position> findByEmployeeId(int employeeId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Position p WHERE p.id = ?1")
    Optional<Position> findByIdForUpdate(int positionId);
}
