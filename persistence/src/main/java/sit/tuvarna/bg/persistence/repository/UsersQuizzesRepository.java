package sit.tuvarna.bg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.entity.UsersQuizzes;

import java.util.List;
import java.util.UUID;

public interface UsersQuizzesRepository extends JpaRepository<UsersQuizzes, UUID> {
    List<UsersQuizzes> getUsersQuizzesByUser(User user);

    @Query("""
    SELECT COUNT(DISTINCT q.category) 
      FROM UsersQuizzes uq 
      JOIN uq.quiz q 
     WHERE uq.user = ?1
    """)
    Integer countDistinctQuizCategoriesByUser(User user);

    List<UsersQuizzes> getUsersQuizzesByUserAndQuiz(User user, Quiz quiz);

    List<UsersQuizzes> getUsersQuizzesByQuiz(Quiz quiz);
}
