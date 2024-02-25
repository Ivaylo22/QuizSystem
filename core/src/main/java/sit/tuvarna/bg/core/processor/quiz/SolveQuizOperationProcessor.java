package sit.tuvarna.bg.core.processor.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.QuizNotFoundException;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.api.operations.quiz.solve.SolveQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.solve.SolveQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.solve.SolveQuizResponse;
import sit.tuvarna.bg.core.processor.achievement.AchievementService;
import sit.tuvarna.bg.persistence.entity.Achievement;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.entity.UsersQuizzes;
import sit.tuvarna.bg.persistence.repository.AchievementRepository;
import sit.tuvarna.bg.persistence.repository.QuizRepository;
import sit.tuvarna.bg.persistence.repository.UserRepository;
import sit.tuvarna.bg.persistence.repository.UsersQuizzesRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SolveQuizOperationProcessor implements SolveQuizOperation {

    @Value("${secondsForFastSolve}")
    Integer secondsForFastSolve;

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;

    private final UsersQuizzesRepository usersQuizzesRepository;

    private final AchievementService achievementService;

    @Override
    public SolveQuizResponse process(SolveQuizRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);

        Quiz quiz = quizRepository.findById(UUID.fromString(request.getQuizId()))
                .orElseThrow(QuizNotFoundException::new);

        updateUserInfo(user, request);
        Integer experienceGained = calculateExperience(request);

        usersQuizzesRepository.save(
                UsersQuizzes
                        .builder()
                        .user(user)
                        .quiz(quiz)
                        .correctAnswers(request.getCorrectAnswers())
                        .secondsToSolve(request.getSecondsToSolve())
                        .isTaken(request.getCorrectAnswers() >= 8)
                        .experienceGained(experienceGained)
                        .build()
        );

        updateUserExperience(user, quiz, experienceGained);

        List<Achievement> earnedAchievements =
                achievementService.getNewAchievements(user, achievementRepository.findAll());

        if (!earnedAchievements.isEmpty()) {
            user.getAchievements().addAll(earnedAchievements);
            userRepository.save(user);
        }
        return SolveQuizResponse.builder().build();
    }

    private void updateUserInfo(User user, SolveQuizRequest request) {
        if (request.getSecondsToSolve() < secondsForFastSolve) {
            user.setQuizzesUnderOneMinuteCount(user.getQuizzesUnderOneMinuteCount() + 1);
        }

        if (request.getCorrectAnswers() == 10) {
            user.setPerfectQuizzesCount(user.getPerfectQuizzesCount() + 1);
        }

        if (request.getCorrectAnswers() >= 8) {
            user.setConsecutiveQuizzesPassedCount(user.getConsecutiveQuizzesPassedCount() + 1);
        }

        if (request.getCorrectAnswers() < 8) {
            user.setConsecutiveQuizzesPassedCount(0);
        }

        if (request.getIsDaily() && request.getCorrectAnswers() >= 8 && !Objects.equals(user.getLastDailyQuizId(), request.getQuizId())) {
            LocalDate lastDailyQuizDate = user.getLastDailyQuizTime() != null
                    ? user.getLastDailyQuizTime().toLocalDateTime().toLocalDate()
                    : null;
            LocalDate today = LocalDate.now();

            if (lastDailyQuizDate != null && ChronoUnit.DAYS.between(lastDailyQuizDate, today) == 1) {
                user.setConsecutiveDailyQuizzesCount(user.getConsecutiveDailyQuizzesCount() + 1);
            } else {
                user.setConsecutiveDailyQuizzesCount(1);
            }
            user.setDailyQuizzesCount(user.getDailyQuizzesCount() + 1);
            user.setLastDailyQuizTime(Timestamp.valueOf(LocalDateTime.now()));
            user.setLastDailyQuizId(request.getQuizId());
        }

        if (request.getCorrectAnswers() > 8) {
            user.setQuizzesPassedCount(user.getQuizzesPassedCount() + 1);
        }

        userRepository.save(user);
    }

    private Integer calculateExperience(SolveQuizRequest request) {
        int experience = 100;
        Integer correctAnswers = request.getCorrectAnswers();
        Integer secondsToSolve = request.getSecondsToSolve();

        double timeCoefficient = 0.0;

        if (secondsToSolve < 60)
            timeCoefficient = 1.0;

        else if (secondsToSolve < 120)
            timeCoefficient = 0.8;

        else if (secondsToSolve < 180)
            timeCoefficient = 0.5;

        else
            timeCoefficient = 0.1;

        double correctAnswersCoefficient = switch (correctAnswers) {
            case 1, 2, 3, 4, 5 -> 0.1;
            case 6, 7 -> 0.5;
            case 8, 9 -> 0.8;
            default -> 1.0;
        };

        double calculatedExperience = experience * correctAnswersCoefficient * timeCoefficient;

        return (int) calculatedExperience;
    }

    public void updateUserExperience(User user, Quiz quiz, Integer newExperience) {
        List<UsersQuizzes> usersQuizzesList = usersQuizzesRepository.getUsersQuizzesByUserAndQuiz(user, quiz);

        Integer maxExperience = 0;
        UsersQuizzes maxExperienceEntry = null;
        for (UsersQuizzes usersQuizzes : usersQuizzesList) {
            if (usersQuizzes.getExperienceGained() > maxExperience) {
                maxExperience = usersQuizzes.getExperienceGained();
                maxExperienceEntry = usersQuizzes;
            }
        }

        if (newExperience > maxExperience) {
            if (maxExperienceEntry != null) {
                user.setExperience(user.getExperience() - maxExperienceEntry.getExperienceGained());
            }
            user.setExperience(user.getExperience() + newExperience);
        }

        userRepository.save(user);
    }
}
