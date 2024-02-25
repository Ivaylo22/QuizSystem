package sit.tuvarna.bg.core.processor.achievement;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.operations.achievement.create.CreateAchievementOperation;
import sit.tuvarna.bg.api.operations.achievement.create.CreateAchievementRequest;
import sit.tuvarna.bg.api.operations.achievement.create.CreateAchievementResponse;
import sit.tuvarna.bg.persistence.entity.Achievement;
import sit.tuvarna.bg.persistence.repository.AchievementRepository;

@Service
@RequiredArgsConstructor
public class CreateAchievementOperationProcessor implements CreateAchievementOperation {

    private final AchievementRepository achievementRepository;
    private final ConversionService conversionService;



    @Override
    public CreateAchievementResponse process(CreateAchievementRequest request) {
        Achievement achievement = conversionService.convert(request, Achievement.class);

        achievementRepository.save(achievement);

        return conversionService.convert(achievement, CreateAchievementResponse.class);
    }
}
