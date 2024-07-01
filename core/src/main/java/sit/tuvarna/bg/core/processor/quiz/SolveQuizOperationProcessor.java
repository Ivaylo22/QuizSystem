package sit.tuvarna.bg.core.processor.quiz;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.enums.NotificationType;
import sit.tuvarna.bg.api.exception.QuizNotFoundException;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.api.operations.quiz.solve.SolveQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.solve.SolveQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.solve.SolveQuizResponse;
import sit.tuvarna.bg.core.externalservices.NotificationService;
import sit.tuvarna.bg.core.externalservices.XPProgress;
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
    private final NotificationService notificationService;

    private static final int SECONDS_PER_QUESTION_FAST = 20;

    @Override
    @Transactional
    public SolveQuizResponse process(SolveQuizRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);
        Quiz quiz = quizRepository.findById(UUID.fromString(request.getQuizId()))
                .orElseThrow(QuizNotFoundException::new);

        int totalQuestions = quiz.getQuestions().size();
        checkIfDailyQuiz(user, quiz, request.getIsDaily());

        updateUserInfo(user, request, totalQuestions);
        UsersQuizzes usersQuizzes = saveUserQuizResult(user, quiz, request);
        updateUserExperience(user, usersQuizzes);
        updateQuizStatistics(quiz);
        achievementService.updateUserAchievements(user);

        return SolveQuizResponse.builder()
                .isPassed(usersQuizzes.getExperienceGained() > 80)
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
        Quiz quiz = quizRepository.findById(UUID.fromString(request.getQuizId()))
                .orElseThrow(QuizNotFoundException::new);
        List<UsersQuizzes> previousAttempts = usersQuizzesRepository.getUsersQuizzesByUserAndQuiz(user, quiz);

        boolean hasPreviousFastAttempt = previousAttempts.stream().anyMatch(attempt ->
                attempt.getSecondsToSolve() / attempt.getQuiz().getQuestions().size() < SECONDS_PER_QUESTION_FAST);

        boolean hasPreviousPerfectAttempt = previousAttempts.stream().anyMatch(attempt ->
                attempt.getCorrectAnswers().equals(attempt.getQuiz().getQuestions().size()));

        boolean hasPreviousPassedAttempt = previousAttempts.stream().anyMatch(UsersQuizzes::getIsPassed);

        int secondsToSolvePerQuestion = request.getSecondsToSolve() / totalQuestions;
        boolean isCurrentAttemptFast = secondsToSolvePerQuestion < SECONDS_PER_QUESTION_FAST;
        boolean isCurrentAttemptPerfect = request.getCorrectAnswers() == totalQuestions;
        boolean isCurrentAttemptPassed = request.getCorrectAnswers() >= (totalQuestions * 0.8);

        if (isCurrentAttemptFast && !hasPreviousFastAttempt) {
            user.setFastQuizzesCount(user.getFastQuizzesCount() + 1);
        }

        if (isCurrentAttemptPerfect && !hasPreviousPerfectAttempt) {
            user.setPerfectQuizzesCount(user.getPerfectQuizzesCount() + 1);
        }

        if (isCurrentAttemptPassed && !hasPreviousPassedAttempt) {
            user.setQuizzesPassedCount(user.getQuizzesPassedCount() + 1);
            user.setConsecutiveQuizzesPassedCount(user.getConsecutiveQuizzesPassedCount() + 1);
        } else if (!isCurrentAttemptPassed && quiz.getIsDaily()) {
            user.setConsecutiveDailyQuizzesCount(0);
            user.setConsecutiveQuizzesPassedCount(0);
        } else if (!isCurrentAttemptPassed) {
            user.setConsecutiveQuizzesPassedCount(0);
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

    private void updateUserExperience(User user, UsersQuizzes userQuiz) {
        List<UsersQuizzes> allUsersQuizzes = usersQuizzesRepository
                .getUsersQuizzesByUserAndQuiz(userQuiz.getUser(), userQuiz.getQuiz());

        int highestPreviousExperience = allUsersQuizzes.stream()
                .filter(uq -> !uq.getId().equals(userQuiz.getId()))
                .mapToInt(UsersQuizzes::getExperienceGained)
                .max()
                .orElse(0);

        int experienceToAdd = 0;
        if (userQuiz.getExperienceGained() > highestPreviousExperience) {
            experienceToAdd = userQuiz.getExperienceGained() - highestPreviousExperience;
        }

        if (experienceToAdd > 0) {
            user.setExperience(user.getExperience() + experienceToAdd);
        }

        XPProgress xpProgress = new XPProgress(user.getExperience());

        if (user.getLevel() != xpProgress.getLevel()) {
            notificationService.sendNotificationToUser(NotificationType.LEVEL_UP, user.getEmail(), xpProgress.getLevel());
        }
        user.setLevel(xpProgress.getLevel());
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
