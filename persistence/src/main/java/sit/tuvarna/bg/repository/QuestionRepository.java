package sit.tuvarna.bg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.tuvarna.bg.entity.Question;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
}
