package sit.tuvarna.bg.api.operations.quiz.create;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateQuizResponse implements ProcessorResponse {

    private String id;
    private List<String> questionIds;
}
