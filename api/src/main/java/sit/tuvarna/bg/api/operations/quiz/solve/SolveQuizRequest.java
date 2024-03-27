package sit.tuvarna.bg.api.operations.quiz.solve;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Number of correct answers is required")
    private Integer correctAnswers;

    @NotNull(message = "Seconds to solve is required")
    private Integer secondsToSolve;

    @NotNull(message = "Is daily is required")
    private Boolean isDaily;
}
