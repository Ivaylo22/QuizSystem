package sit.tuvarna.bg.operations.quiz.create;

import lombok.*;
import sit.tuvarna.bg.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateQuizResponse implements ProcessorResponse {

    private String id;
}
