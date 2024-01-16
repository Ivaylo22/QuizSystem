package sit.tuvarna.bg.operations.quiz.active;

import lombok.*;
import sit.tuvarna.bg.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActiveQuizResponse implements ProcessorResponse {

    private String isArchived;
}
