package sit.tuvarna.bg.core.processor.achievement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.persistence.entity.Achievement;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.repository.UserRepository;
import sit.tuvarna.bg.persistence.repository.UsersQuizzesRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final UsersQuizzesRepository usersQuizzesRepository;
    private final UserRepository userRepository;



    public List<Achievement> getNewAchievements(User user, List<Achievement> achievements) {
        List<Achievement> earnedAchievements = new ArrayList<>();

        for (Achievement achievement : achievements) {
            if (user.getAchievements().contains(achievement)) {
                continue;
            }

            if (userMeetsCriteria(user, achievement)) {
                earnedAchievements.add(achievement);
                user.setAchievementPoints(user.getAchievementPoints() + achievement.getPoints());
                userRepository.save(user);
            }
        }

        return earnedAchievements;
    }

    private boolean userMeetsCriteria(User user, Achievement achievement) {
        switch (achievement.getName()) {
            case "Fast" -> {
                return user.getQuizzesUnderOneMinuteCount() >= 1;
            }
            case "Faster" -> {
                return user.getQuizzesUnderOneMinuteCount() >= 5;
            }
            case "The Fastest" -> {
                return user.getQuizzesUnderOneMinuteCount() >= 10;
            }
            case "Top Scorer: 1x10" -> {
                return user.getPerfectQuizzesCount() >= 1;
            }
            case "Top Scorer: 5x10" -> {
                return user.getPerfectQuizzesCount() >= 5;
            }
            case "Top Scorer: 10x10" -> {
                return user.getPerfectQuizzesCount() >= 10;
            }
            case "Consistent: 3x80%" -> {
                return user.getConsecutiveQuizzesPassedCount() >= 3;
            }
            case "Consistent: 5x80%" -> {
                return user.getConsecutiveQuizzesPassedCount() >= 5;
            }
            case "Consistent: 10x80%" -> {
                return user.getConsecutiveQuizzesPassedCount() >= 10;
            }
            case "Diverse: 3 Categories" -> {
                return usersQuizzesRepository.countDistinctQuizCategoriesByUser(user) >= 3;
            }
            case "Diverse: 5 Categories" -> {
                return usersQuizzesRepository.countDistinctQuizCategoriesByUser(user) >= 5;
            }
            case "Diverse: 10 Categories" -> {
                return usersQuizzesRepository.countDistinctQuizCategoriesByUser(user) >= 10;
            }
            case "Persistent: 3 Daily" -> {
                return user.getConsecutiveDailyQuizzesCount() >= 3;
            }
            case "Persistent: 5 Daily" -> {
                return user.getConsecutiveDailyQuizzesCount() >= 5;
            }
            case "Persistent: 10 Daily" -> {
                return user.getConsecutiveDailyQuizzesCount() >= 10;
            }
            case "Enthusiast: 10" -> {
                return user.getQuizzesPassedCount() >= 10;
            }
            case "Enthusiast: 25" -> {
                return user.getQuizzesPassedCount() >= 25;
            }
            case "Enthusiast: 50" -> {
                return user.getQuizzesPassedCount() >= 50;
            }
            default -> {
                return false;
            }
        }
    }
}