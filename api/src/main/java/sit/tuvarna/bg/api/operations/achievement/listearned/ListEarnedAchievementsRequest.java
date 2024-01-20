package sit.tuvarna.bg.api.operations.achievement.listearned;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ListEarnedAchievementsRequest implements ProcessorRequest {

    @NotBlank(message = "User email is required")
    private String userEmail;
}
