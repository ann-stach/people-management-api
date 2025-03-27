package pl.kurs._import.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs._import.model.ImportStatus;

public interface ImportStatusRepository extends JpaRepository<ImportStatus, Integer> {
}
