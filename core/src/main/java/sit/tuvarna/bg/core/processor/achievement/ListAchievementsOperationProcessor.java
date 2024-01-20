package sit.tuvarna.bg.core.processor.achievement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.model.AchievementModel;
import sit.tuvarna.bg.api.operations.achievement.list.ListAchievementsOperation;
import sit.tuvarna.bg.api.operations.achievement.list.ListAchievementsRequest;
import sit.tuvarna.bg.api.operations.achievement.list.ListAchievementsResponse;
import sit.tuvarna.bg.persistence.entity.Achievement;
import sit.tuvarna.bg.persistence.repository.AchievementRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListAchievementsOperationProcessor implements ListAchievementsOperation {

    private final AchievementRepository achievementRepository;
    private final ConversionService conversionService;

    @Autowired
    public ListAchievementsOperationProcessor(AchievementRepository achievementRepository,
                                              ConversionService conversionService) {
        this.achievementRepository = achievementRepository;
        this.conversionService = conversionService;
    }

    @Override
    public ListAchievementsResponse process(ListAchievementsRequest request) {
        List<Achievement> achievements = achievementRepository.findAll();

        List<AchievementModel> achievementModels = achievements.stream()
                .map(ach -> conversionService.convert(ach, AchievementModel.class))
                .toList();

        return ListAchievementsResponse
                .builder()
                .achievements(achievementModels)
                .build();
    }
}
