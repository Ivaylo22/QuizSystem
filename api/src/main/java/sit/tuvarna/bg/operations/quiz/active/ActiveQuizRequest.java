package sit.tuvarna.bg.operations.quiz.active;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import sit.tuvarna.bg.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActiveQuizRequest implements ProcessorRequest {

    @UUID
    @NotBlank(message = "ID is required")
    private String id;
}
