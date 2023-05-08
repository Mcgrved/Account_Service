package account.repositories;


import account.models.entities.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
@EnableJpaRepositories
public interface UserRepository extends CrudRepository<User, Long> {
   User findByUserId(long userId);
   User findByName(String name);
   User findByNameAndLastname(String name, String lastName);
   User findByEmailIgnoreCase(String email);
   @Query(value = "UPDATE users u SET u.failedAttempts = ?1 WHERE u.email = ?2",
   nativeQuery = true)
   @Modifying(flushAutomatically = true)
   void updateFailedAttempts(int failedAttempts, String email);
}
