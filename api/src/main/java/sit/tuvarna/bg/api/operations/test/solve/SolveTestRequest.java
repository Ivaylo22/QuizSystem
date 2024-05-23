package sit.tuvarna.bg.api.operations.test.solve;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;
import sit.tuvarna.bg.api.model.QuestionAttemptModel;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SolveTestRequest implements ProcessorRequest {

    private String email;
    private String testId;
    private List<QuestionAttemptModel> questionAttempts;
    private LocalDateTime attemptTime;
    private Double totalPoints;
    private Double finalScore;
}
