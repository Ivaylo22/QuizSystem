package sit.tuvarna.bg.api.operations.quiz.solve;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolveQuizResponse implements ProcessorResponse {

    private Integer experienceGained;

    private Boolean isPassed;
}
