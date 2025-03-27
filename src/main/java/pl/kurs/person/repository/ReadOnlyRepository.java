package pl.kurs.person.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@NoRepositoryBean
@Transactional(readOnly = true)
public interface ReadOnlyRepository<T, ID> extends PagingAndSortingRepository<T, ID> {
    Page<T> findAll(Pageable pageable);
    Optional<T> findById(int id);

}
