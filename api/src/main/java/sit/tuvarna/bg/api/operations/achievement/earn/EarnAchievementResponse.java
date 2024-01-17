package sit.tuvarna.bg.api.operations.achievement.earn;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EarnAchievementResponse implements ProcessorResponse {

    private Integer achievementPoints;
}
