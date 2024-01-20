package sit.tuvarna.bg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.tuvarna.bg.persistence.entity.UsersQuizzes;

import java.util.UUID;

public interface UsersQuizzesRepository extends JpaRepository<UsersQuizzes, UUID> {
}