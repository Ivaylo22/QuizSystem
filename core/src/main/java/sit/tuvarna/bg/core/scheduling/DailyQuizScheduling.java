package sit.tuvarna.bg.core.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.enums.QuizStatus;
import sit.tuvarna.bg.persistence.repository.QuizRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyQuizScheduling {
    private final QuizRepository quizRepository;

    @Transactional
    public void updateDailyQuiz() {
        List<Quiz> activeQuizzes = quizRepository.findAll().stream()
                .filter(q -> q.getStatus() == QuizStatus.ACTIVE)
                .toList();

        if (activeQuizzes.isEmpty()) {
            log.warn("No active quizzes available to set as daily.");
            return;
        }

        quizRepository.resetDailyQuiz();

        Random random = new Random();
        Quiz dailyQuiz = activeQuizzes.get(random.nextInt(activeQuizzes.size()));
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
