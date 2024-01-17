package sit.tuvarna.bg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.tuvarna.bg.entity.Answer;

import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
}
