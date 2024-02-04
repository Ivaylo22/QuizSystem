package sit.tuvarna.bg.api.operations.quiz.approve;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApproveQuizResponse implements ProcessorResponse {
    private Boolean isRequired;
}
