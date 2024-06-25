package sit.tuvarna.bg.api.operations.quiz.approve;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApproveQuizResponse implements ProcessorResponse {
    private String status;
}
