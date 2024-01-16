package sit.tuvarna.bg.operations.achievement.list;

import lombok.*;
import sit.tuvarna.bg.base.ProcessorResponse;
import sit.tuvarna.bg.model.AchievementModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ListAchievementsResponse implements ProcessorResponse {

    private List<AchievementModel> achievements;
}
