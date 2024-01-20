package sit.tuvarna.bg.core.processor.achievement;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.api.model.AchievementModel;
import sit.tuvarna.bg.api.operations.achievement.listearned.ListEarnedAchievementsOperation;
import sit.tuvarna.bg.api.operations.achievement.listearned.ListEarnedAchievementsRequest;
import sit.tuvarna.bg.api.operations.achievement.listearned.ListEarnedAchievementsResponse;
import sit.tuvarna.bg.persistence.entity.Achievement;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.repository.UserRepository;

import java.util.List;

@Service
public class ListEarnedAchievementsOperationProcessor implements ListEarnedAchievementsOperation {

    private final UserRepository userRepository;
    private final ConversionService conversionService;

    public ListEarnedAchievementsOperationProcessor(UserRepository userRepository,
                                                    ConversionService conversionService) {
        this.userRepository = userRepository;
        this.conversionService = conversionService;
    }

    @Override
    public ListEarnedAchievementsResponse process(ListEarnedAchievementsRequest request) {
        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(UserNotFoundException::new);

        List<Achievement> achievements = user.getAchievements();

        List<AchievementModel> achievementModels = achievements.stream()
                .map(ach -> conversionService.convert(ach, AchievementModel.class))
                .toList();

        return ListEarnedAchievementsResponse
                .builder()
                .achievements(achievementModels)
                .build();
    }
}
