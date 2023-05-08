package account.repositories;

import account.models.entities.SecurityEvent;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface SecurityEventRepository extends CrudRepository<SecurityEvent, Long> {
    @Override
    List<SecurityEvent> findAll();
    //SecurityEvent findBySecurityEventId(long id);
}
