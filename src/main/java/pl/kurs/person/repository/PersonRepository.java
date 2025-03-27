package pl.kurs.person.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.kurs.person.model.Person;


public interface PersonRepository extends JpaRepository<Person, Integer>, JpaSpecificationExecutor<Person> {


}
