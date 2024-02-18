package sit.tuvarna.bg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.persistence.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.achievements WHERE u.email = :email")
    Optional<User> findByEmailIncludingAchievements(String email);
}
