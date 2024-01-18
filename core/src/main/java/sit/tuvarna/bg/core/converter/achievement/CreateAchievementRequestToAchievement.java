package sit.tuvarna.bg.core.converter.achievement;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sit.tuvarna.bg.api.operations.achievement.create.CreateAchievementRequest;
import sit.tuvarna.bg.persistence.entity.Achievement;

@Component
public class CreateAchievementRequestToAchievement
        implements Converter<CreateAchievementRequest, Achievement>{
    @Override
    public Achievement convert(CreateAchievementRequest source) {
        return Achievement
                .builder()
                .name(source.getName())
                .description(source.getDescription())
                .points(source.getAchievementPoints())
                .build();
    }
}
