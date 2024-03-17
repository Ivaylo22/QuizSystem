package sit.tuvarna.bg.core.processor.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.QuizNotFoundException;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.api.operations.quiz.solve.SolveQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.solve.SolveQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.solve.SolveQuizResponse;
import sit.tuvarna.bg.core.processor.achievement.AchievementService;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.entity.UsersQuizzes;
import sit.tuvarna.bg.persistence.repository.QuizRepository;
import sit.tuvarna.bg.persistence.repository.UserRepository;
import sit.tuvarna.bg.persistence.repository.UsersQuizzesRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SolveQuizOperationProcessor implements SolveQuizOperation {
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final UsersQuizzesRepository usersQuizzesRepository;
    private final AchievementService achievementService;

    private static final int SECONDS_PER_QUESTION_FAST = 20;

    @Override
    public SolveQuizResponse process(SolveQuizRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);
        Quiz quiz = quizRepository.findById(UUID.fromString(request.getQuizId()))
                .orElseThrow(QuizNotFoundException::new);

        int totalQuestions = quiz.getQuestions().size();
        checkIfDailyQuiz(user, quiz, request.getIsDaily());

        UsersQuizzes usersQuizzes = saveUserQuizResult(user, quiz, request);
        updateUserExperience(user, usersQuizzes.getExperienceGained());
        updateUserInfo(user, request, totalQuestions);
        updateQuizStatistics(quiz);
        achievementService.updateUserAchievements(user);

        return SolveQuizResponse.builder()
                .isPassed(request.getCorrectAnswers() >= 8)
                .experienceGained(usersQuizzes.getExperienceGained())
                .build();
    }

    private void checkIfDailyQuiz(User user, Quiz quiz, boolean isDaily) {
        if (isDaily) {
            LocalDate today = LocalDate.now();
            LocalDate lastDailyQuizDate = user.getLastDailyQuizTime() != null ?
                    user.getLastDailyQuizTime().toLocalDateTime().toLocalDate() : null;

            if (lastDailyQuizDate == null || !lastDailyQuizDate.equals(today)) {
                user.setLastDailyQuizTime(Timestamp.valueOf(LocalDateTime.now()));
                user.setLastDailyQuizId(quiz.getId().toString());
                user.setDailyQuizzesCount(user.getDailyQuizzesCount() + 1);

                if (lastDailyQuizDate != null && ChronoUnit.DAYS.between(lastDailyQuizDate, today) == 1) {
                    user.setConsecutiveDailyQuizzesCount(user.getConsecutiveDailyQuizzesCount() + 1);
                } else {
                    user.setConsecutiveDailyQuizzesCount(1); // Reset if not consecutive
                }
            }
            userRepository.save(user);
        }
    }

    private UsersQuizzes saveUserQuizResult(User user, Quiz quiz, SolveQuizRequest request) {
        boolean isPassed = request.getCorrectAnswers() >= (quiz.getQuestions().size() * 0.8);
        UsersQuizzes usersQuizzes = UsersQuizzes.builder()
                .user(user)
                .quiz(quiz)
                .correctAnswers(request.getCorrectAnswers())
                .secondsToSolve(request.getSecondsToSolve())
                .isTaken(true)
                .isPassed(isPassed)
                .experienceGained(calculateExperience(request, quiz))
                .build();
        return usersQuizzesRepository.save(usersQuizzes);
    }

    private void updateUserInfo(User user, SolveQuizRequest request, int totalQuestions) {
        int secondsToSolvePerQuestion = request.getSecondsToSolve() / totalQuestions;

        if (secondsToSolvePerQuestion < SECONDS_PER_QUESTION_FAST) {
            user.setFastQuizzesCount(user.getFastQuizzesCount() + 1);
        }

        if (request.getCorrectAnswers() == totalQuestions) {
            user.setPerfectQuizzesCount(user.getPerfectQuizzesCount() + 1);
        }

        boolean isPassed = request.getCorrectAnswers() >= (totalQuestions * 0.8);
        if (isPassed) {
            user.setQuizzesPassedCount(user.getQuizzesPassedCount() + 1);
            user.setConsecutiveQuizzesPassedCount(user.getConsecutiveQuizzesPassedCount() + 1);
        } else {
            user.setConsecutiveQuizzesPassedCount(0); // Reset consecutive pass count on failure
        }

        userRepository.save(user);
    }

    private Integer calculateExperience(SolveQuizRequest request, Quiz quiz) {
        final int MAX_EXPERIENCE = 100;
        int totalQuestions = quiz.getQuestions().size();
        int correctAnswers = request.getCorrectAnswers();
        int secondsToSolve = request.getSecondsToSolve();

        int baseTimeForMaxExperience = totalQuestions * 30; // Assuming 30 seconds per question for max experience
        double correctAnswersProportion = (double) correctAnswers / totalQuestions;
        double timeCoefficient = Math.min(1.0, (double) baseTimeForMaxExperience / secondsToSolve);
        double calculatedExperience = MAX_EXPERIENCE * correctAnswersProportion * timeCoefficient;

        return (int) Math.round(calculatedExperience);
    }

    private void updateUserExperience(User user, Integer experienceGained) {
        user.setExperience(user.getExperience() + experienceGained);
        userRepository.save(user);
    }

    private void updateQuizStatistics(Quiz quiz) {
        List<UsersQuizzes> usersQuizzes = usersQuizzesRepository.getUsersQuizzesByQuiz(quiz);

        if (usersQuizzes != null && !usersQuizzes.isEmpty()) {
            double totalSeconds = usersQuizzes.stream()
                    .mapToDouble(UsersQuizzes::getSecondsToSolve)
                    .sum();

            double totalCorrectAnswers = usersQuizzes.stream()
                    .mapToDouble(UsersQuizzes::getCorrectAnswers)
                    .sum();

            Integer averageSeconds = (int) Math.ceil(totalSeconds / usersQuizzes.size());
            Double averageScore = totalCorrectAnswers / usersQuizzes.size();

            quiz.setAverageSecondsNeeded(averageSeconds);
            quiz.setAverageCorrectAnswers(averageScore);

            quizRepository.save(quiz);
        }
    }
}
