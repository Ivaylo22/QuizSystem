package sit.tuvarna.bg.core.processor.achievement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.AchievementAlreadyEarnedException;
import sit.tuvarna.bg.api.exception.AchievementNotFoundException;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.api.operations.achievement.earn.EarnAchievementOperation;
import sit.tuvarna.bg.api.operations.achievement.earn.EarnAchievementRequest;
import sit.tuvarna.bg.api.operations.achievement.earn.EarnAchievementResponse;
import sit.tuvarna.bg.persistence.entity.Achievement;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.repository.AchievementRepository;
import sit.tuvarna.bg.persistence.repository.UserRepository;

@Service
public class EarnAchievementOperationProcessor implements EarnAchievementOperation {

    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final ConversionService conversionService;

    @Autowired
    public EarnAchievementOperationProcessor(UserRepository userRepository,
                                             AchievementRepository achievementRepository,
                                             ConversionService conversionService) {
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.conversionService = conversionService;
    }

    @Override
    public EarnAchievementResponse process(EarnAchievementRequest request) {
        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(UserNotFoundException::new);

        Achievement achievement = achievementRepository.findById(request.getAchievementId())
                .orElseThrow(AchievementNotFoundException::new);

        if (!user.getAchievements().contains(achievement)) {
            user.setAchievementPoints(user.getAchievementPoints() + achievement.getPoints());
            user.getAchievements().add(achievement);

            userRepository.save(user);
        } else {
            throw new AchievementAlreadyEarnedException();
        }

        return conversionService.convert(user, EarnAchievementResponse.class);
    }
}
