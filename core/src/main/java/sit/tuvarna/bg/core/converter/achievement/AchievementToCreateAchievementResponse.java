package sit.tuvarna.bg.core.converter.achievement;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sit.tuvarna.bg.api.operations.achievement.create.CreateAchievementResponse;
import sit.tuvarna.bg.persistence.entity.Achievement;

@Component
public class AchievementToCreateAchievementResponse
        implements Converter<Achievement, CreateAchievementResponse> {
    @Override
    public CreateAchievementResponse convert(Achievement source) {
        return CreateAchievementResponse
                .builder()
                .id(String.valueOf(source.getId()))
                .build();
    }
}
