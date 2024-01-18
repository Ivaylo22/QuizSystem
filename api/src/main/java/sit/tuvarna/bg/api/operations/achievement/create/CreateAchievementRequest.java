package sit.tuvarna.bg.api.operations.achievement.create;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateAchievementRequest implements ProcessorRequest {

    @NotBlank(message = "Achievement name is required")
    private String name;

    @NotBlank(message = "Achievement description is required")
    private String description;

    @NotBlank(message = "Achievement points are required")
    private Integer achievementPoints;
}
