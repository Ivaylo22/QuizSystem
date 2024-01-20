package sit.tuvarna.bg.core.converter.achievement;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sit.tuvarna.bg.api.operations.achievement.earn.EarnAchievementResponse;
import sit.tuvarna.bg.persistence.entity.User;

@Component
public class UserToEarnAchievementResponse implements Converter<User, EarnAchievementResponse> {
    @Override
    public EarnAchievementResponse convert(User source) {
        return EarnAchievementResponse
                .builder()
                .achievementPoints(source.getAchievementPoints())
                .build();
    }
}
