package pl.kurs.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.security.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
