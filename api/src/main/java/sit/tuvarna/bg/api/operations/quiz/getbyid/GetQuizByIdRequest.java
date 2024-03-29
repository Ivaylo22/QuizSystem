package sit.tuvarna.bg.api.operations.quiz.getbyid;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetQuizByIdRequest implements ProcessorRequest {

    @UUID
    @NotBlank(message = "Quiz id is required")
    private String quizId;
}
