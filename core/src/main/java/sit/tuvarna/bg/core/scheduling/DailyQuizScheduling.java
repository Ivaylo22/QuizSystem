package sit.tuvarna.bg.core.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.repository.QuizRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j

public class DailyQuizScheduling {
    private final QuizRepository quizRepository;

    @Transactional
    public void updateDailyQuiz() {
        List<Quiz> quizzes = quizRepository.findAll();
        if (quizzes.isEmpty()) {
            log.warn("No quizzes available to set as daily.");
            return;
        }

        quizRepository.resetDailyQuiz();

        Random random = new Random();
        Quiz dailyQuiz = quizzes.get(random.nextInt(quizzes.size()));
        dailyQuiz.setIsDaily(true);
        dailyQuiz.setLastUpdated(LocalDateTime.now(ZoneOffset.UTC));

        quizRepository.save(dailyQuiz);
        log.info("Updated daily quiz: {}", dailyQuiz.getTitle());
    }

    @Transactional
    public void checkAndUpdateDailyQuiz() {
        Optional<Quiz> currentDailyQuizOpt = quizRepository.findDailyQuiz();
        if (currentDailyQuizOpt.isPresent()) {
            Quiz currentDailyQuiz = currentDailyQuizOpt.get();
            LocalDateTime lastUpdated = currentDailyQuiz.getLastUpdated();

            if (lastUpdated == null || !lastUpdated.toLocalDate().isEqual(LocalDateTime.now(ZoneOffset.UTC).toLocalDate())) {
                updateDailyQuiz();
            }
        } else {
            updateDailyQuiz();
        }
    }
}
