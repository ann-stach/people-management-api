package pl.kurs.person.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.kurs.person.model.views.PersonView;

public interface PersonViewRepository extends ReadOnlyRepository<PersonView, Integer>, JpaSpecificationExecutor<PersonView> {

}
