package sit.tuvarna.bg.api.operations.quiz.getbyid;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.model.QuizModel;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetQuizByIdResponse implements ProcessorResponse {

    private QuizModel quizModel;
}
