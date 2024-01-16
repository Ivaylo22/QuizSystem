package sit.tuvarna.bg.operations.achievement.earn;

import lombok.*;
import sit.tuvarna.bg.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EarnAchievementResponse implements ProcessorResponse {

    private Integer achievementPoints;
}
