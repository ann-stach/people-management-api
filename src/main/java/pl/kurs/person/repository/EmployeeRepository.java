package pl.kurs.person.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.kurs.person.model.Employee;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    @Query("""
        SELECT p
        FROM Person p
        LEFT JOIN FETCH p.positions
        WHERE TYPE(p) = Employee AND p.id = :employeeId
    """)
    Optional<Employee> findEmployeeWithPositions(@Param("employeeId") int employeeId);
}
