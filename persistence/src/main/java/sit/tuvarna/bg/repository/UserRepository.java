package sit.tuvarna.bg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.tuvarna.bg.entity.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
