package sit.tuvarna.bg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.persistence.entity.QuestionAttempt;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionAttemptRepository extends JpaRepository<QuestionAttempt, UUID> {

    List<QuestionAttempt> findAllByQuestionIdIn(List<UUID> questionIds);
}