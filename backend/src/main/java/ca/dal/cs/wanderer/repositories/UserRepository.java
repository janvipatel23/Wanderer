package ca.dal.cs.wanderer.repositories;

import ca.dal.cs.wanderer.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
     User findByEmailId(String emailId);
     boolean existsUserByEmailIdIgnoreCase(String emailId);
}
