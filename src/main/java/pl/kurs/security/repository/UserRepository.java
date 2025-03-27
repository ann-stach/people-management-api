package pl.kurs.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.security.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM users u LEFT JOIN FETCH u.roles LEFT JOIN FETCH u.attempts WHERE u.name = :name")
    Optional<User> findByName(String name);

}
