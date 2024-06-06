package sit.tuvarna.bg.api.operations.quiz.getdaily;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetDailyQuizResponse implements ProcessorResponse {
    private String quizId;
    private String title;
    private String category;
    private Integer questionsCount;
    private Integer averageTimeToSolve;
}
