package sit.tuvarna.bg.api.operations.achievement.list;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.model.AchievementModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ListAchievementsResponse implements ProcessorResponse {

    private List<AchievementModel> achievements;
}
