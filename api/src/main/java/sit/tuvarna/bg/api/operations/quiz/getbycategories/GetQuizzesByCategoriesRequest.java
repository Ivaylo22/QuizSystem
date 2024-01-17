package sit.tuvarna.bg.api.operations.quiz.getbycategories;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;
import sit.tuvarna.bg.api.enums.QuizCategory;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetQuizzesByCategoriesRequest implements ProcessorRequest {

    private QuizCategory category;
}
