package sit.tuvarna.bg.operations.achievement.update;

import lombok.*;
import sit.tuvarna.bg.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateAchievementResponse implements ProcessorResponse {

    private String id;
}
