package sit.tuvarna.bg.operations.quiz.updatecategory;

import lombok.*;
import sit.tuvarna.bg.base.ProcessorResponse;
import sit.tuvarna.bg.model.QuizCategory;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateQuizCategoryResponse implements ProcessorResponse {

    private QuizCategory category;
}
