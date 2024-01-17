package sit.tuvarna.bg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.tuvarna.bg.entity.UsersQuizzes;

import java.util.UUID;

public interface UsersQuizzesRepository extends JpaRepository<UsersQuizzes, UUID> {
}
