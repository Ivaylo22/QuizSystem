package sit.tuvarna.bg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.persistence.entity.Answer;
import sit.tuvarna.bg.persistence.entity.Question;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    @Query("SELECT q FROM Question q WHERE q.section.test.id = :testId")
    List<Question> findAllByTestId(@Param("testId") UUID testId);
}
