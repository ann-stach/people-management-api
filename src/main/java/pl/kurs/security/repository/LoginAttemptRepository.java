package pl.kurs.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.kurs.security.model.LoginAttempt;

import java.time.LocalDateTime;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Integer> {

    @Query("SELECT COUNT(l) FROM LoginAttempt l WHERE l.user.id = :userId AND l.successful = false AND l.loginTime > :time")
    long countFailedAttemptsInLast10Minutes(@Param("userId") int userId, @Param("time") LocalDateTime time);
}
