package sit.tuvarna.bg.api.operations.achievement.create;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAchievementResponse implements ProcessorResponse {

    private String id;
}
