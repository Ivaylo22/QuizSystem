package sit.tuvarna.bg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.tuvarna.bg.entity.Quiz;

import java.util.UUID;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {
}
