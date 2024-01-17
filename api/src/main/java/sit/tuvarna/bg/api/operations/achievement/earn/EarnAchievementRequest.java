package sit.tuvarna.bg.api.operations.achievement.earn;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EarnAchievementRequest implements ProcessorRequest {

    @UUID
    @NotBlank(message = "User id is required")
    private String userId;

    @UUID
    @NotBlank(message = "Achievement id is required")
    private String achievementId;
}
