package sit.tuvarna.bg.core.converter.achievement;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sit.tuvarna.bg.api.model.AchievementModel;
import sit.tuvarna.bg.persistence.entity.Achievement;

@Component
public class AchievementToAchievementModel implements Converter<Achievement, AchievementModel> {
    @Override
    public AchievementModel convert(Achievement source) {
        return AchievementModel
                .builder()
                .id(source.getId().toString())
                .name(source.getName())
                .description(source.getDescription())
                .achievementPoints(source.getPoints())
                .build();
    }
}
