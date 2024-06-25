package sit.tuvarna.bg.api.operations.quiz.active;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActiveQuizResponse implements ProcessorResponse {

    private String status;
}
