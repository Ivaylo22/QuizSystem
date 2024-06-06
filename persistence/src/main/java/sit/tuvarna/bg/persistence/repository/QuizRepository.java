package sit.tuvarna.bg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sit.tuvarna.bg.persistence.entity.Category;
import sit.tuvarna.bg.persistence.entity.Quiz;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    List<Quiz> findAllByCategory(Category category);

    List<Quiz> getAllByCreatorEmail(String email);

    @Query("SELECT q FROM Quiz q WHERE q.isDaily = true")
    Optional<Quiz> findDailyQuiz();

    @Modifying
    @Transactional
    @Query("UPDATE Quiz q SET q.isDaily = false WHERE q.isDaily = true")
    void resetDailyQuiz();
}
