package sit.tuvarna.bg.api.operations.quiz.getmyquizzes;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.model.QuizModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetMyQuizzesResponse implements ProcessorResponse {

    private List<QuizModel> quizModelList;
}
