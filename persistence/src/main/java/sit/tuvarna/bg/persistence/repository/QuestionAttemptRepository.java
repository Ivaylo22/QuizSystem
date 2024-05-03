package sit.tuvarna.bg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.persistence.entity.QuestionAttempt;

import java.util.UUID;

@Repository
public interface QuestionAttemptRepository extends JpaRepository<QuestionAttempt, UUID> {
}
