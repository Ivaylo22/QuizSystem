package sit.tuvarna.bg.api.operations.quiz.getrequested;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.model.QuizModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetRequestedQuizzesResponse implements ProcessorResponse {
    private List<QuizModel> quizzes;
}
