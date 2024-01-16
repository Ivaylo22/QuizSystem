package sit.tuvarna.bg.operations.quiz.getunfinished;

import lombok.*;
import sit.tuvarna.bg.base.ProcessorResponse;
import sit.tuvarna.bg.model.QuizModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetUnfinishedQuizzesResponse implements ProcessorResponse {

    private List<QuizModel> quizzes;
}
