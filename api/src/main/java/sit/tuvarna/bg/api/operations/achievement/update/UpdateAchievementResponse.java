package sit.tuvarna.bg.api.operations.achievement.update;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateAchievementResponse implements ProcessorResponse {

    private String id;
}
