package sit.tuvarna.bg.api.operations.quiz.solve;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SolveQuizRequest implements ProcessorRequest {

    @NotBlank(message = "User email is required")
    private String email;

    @UUID
    @NotBlank(message = "Quiz id is required")
    private String quizId;

    private Integer correctAnswers;

    private Integer secondsToSolve;

    private Boolean isDaily;
}
