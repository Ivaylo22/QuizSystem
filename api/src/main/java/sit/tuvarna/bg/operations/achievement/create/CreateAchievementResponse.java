package sit.tuvarna.bg.operations.achievement.create;

import lombok.*;
import sit.tuvarna.bg.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateAchievementResponse implements ProcessorResponse {

    private String id;
}
