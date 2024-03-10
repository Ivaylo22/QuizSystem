package sit.tuvarna.bg.api.operations.quiz.decline;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeclineQuizRequest implements ProcessorRequest {

    @UUID
    @NotBlank(message = "Quiz id is required")
    private String id;
}
