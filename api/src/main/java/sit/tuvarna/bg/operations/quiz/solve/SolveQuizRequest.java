package sit.tuvarna.bg.operations.quiz.solve;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import sit.tuvarna.bg.base.ProcessorRequest;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SolveQuizRequest implements ProcessorRequest {

    @UUID
    @NotBlank(message = "User id is required")
    private String userId;

    @UUID
    @NotBlank(message = "Quiz id is required")
    private String quizId;

    private Integer correctAnswers;

    private Boolean successful;

    private Integer secondsToSolve;
}
