package sit.tuvarna.bg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.persistence.entity.Question;
import sit.tuvarna.bg.persistence.entity.QuestionAttempt;
import sit.tuvarna.bg.persistence.entity.UsersTests;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionAttemptRepository extends JpaRepository<QuestionAttempt, UUID> {

    List<QuestionAttempt> findAllByQuestionIdIn(List<UUID> questionIds);

    Optional<QuestionAttempt> findByUsersTestsAndQuestion(UsersTests usersTests, Question question);
}