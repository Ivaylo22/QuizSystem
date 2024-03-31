package sit.tuvarna.bg.core.processor.achievement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.persistence.entity.Achievement;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.repository.AchievementRepository;
import sit.tuvarna.bg.persistence.repository.UserRepository;
import sit.tuvarna.bg.persistence.repository.UsersQuizzesRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final UsersQuizzesRepository usersQuizzesRepository;
    private final AchievementRepository achievementRepository;
    private final UserRepository userRepository;

    public void updateUserAchievements(User user) {
        List<Achievement> allAchievements = achievementRepository.findAll();
        List<Achievement> earnedAchievements = new ArrayList<>();

        for (Achievement achievement : allAchievements) {
            if (!user.getAchievements().contains(achievement) && userMeetsCriteria(user, achievement)) {
                earnedAchievements.add(achievement);
                user.getAchievements().add(achievement);
                user.setAchievementPoints(user.getAchievementPoints() + achievement.getPoints());
            }
        }

        if (!earnedAchievements.isEmpty()) {
            userRepository.save(user);
        }
    }

    private boolean userMeetsCriteria(User user, Achievement achievement) {
        switch (achievement.getName()) {
            case "Бърз" -> {
                return user.getFastQuizzesCount() >= 1;
            }
            case "Скоростен" -> {
                return user.getFastQuizzesCount() >= 5;
            }
            case "Светкавичен" -> {
                return user.getFastQuizzesCount() >= 10;
            }
            case "Перфектен" -> {
                return user.getPerfectQuizzesCount() >= 1;
            }
            case "Неповторим" -> {
                return user.getPerfectQuizzesCount() >= 5;
            }
            case "Брилянтен" -> {
                return user.getPerfectQuizzesCount() >= 10;
            }
            case "Постоянен" -> {
                return user.getConsecutiveQuizzesPassedCount() >= 3;
            }
            case "Неуморен" -> {
                return user.getConsecutiveQuizzesPassedCount() >= 5;
            }
            case "Съвършен" -> {
                return user.getConsecutiveQuizzesPassedCount() >= 10;
            }
            case "Разнообразен" -> {
                return usersQuizzesRepository.countDistinctQuizCategoriesByUser(user) >= 3;
            }
            case "По-разнообразен" -> {
                return usersQuizzesRepository.countDistinctQuizCategoriesByUser(user) >= 5;
            }
            case "Най-разнообразен" -> {
                return usersQuizzesRepository.countDistinctQuizCategoriesByUser(user) >= 10;
            }
            case "Упоритост: 3 дни поред" -> {
                return user.getConsecutiveDailyQuizzesCount() >= 3;
            }
            case "Упоритост: 5 дни поред" -> {
                return user.getConsecutiveDailyQuizzesCount() >= 5;
            }
            case "Упоритост: 10 дни поред" -> {
                return user.getConsecutiveDailyQuizzesCount() >= 10;
            }
            case "Ентусиаст" -> {
                return user.getQuizzesPassedCount() >= 10;
            }
            case "Професионалист" -> {
                return user.getQuizzesPassedCount() >= 25;
            }
            case "Шампион" -> {
                return user.getQuizzesPassedCount() >= 50;
            }
            default -> {
                return false;
            }
        }
    }
}