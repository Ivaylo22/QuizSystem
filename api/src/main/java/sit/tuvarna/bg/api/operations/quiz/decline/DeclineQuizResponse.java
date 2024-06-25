package sit.tuvarna.bg.api.operations.quiz.decline;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeclineQuizResponse implements ProcessorResponse {

    private String status;
}
