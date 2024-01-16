package sit.tuvarna.bg.operations.quiz.solve;

import lombok.*;
import sit.tuvarna.bg.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SolveQuizResponse implements ProcessorResponse {

    private Integer experienceGained;
}
