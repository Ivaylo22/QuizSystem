package sit.tuvarna.bg.api.operations.quiz.updatecategory;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.enums.QuizCategory;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateQuizCategoryResponse implements ProcessorResponse {

    private QuizCategory category;
}
