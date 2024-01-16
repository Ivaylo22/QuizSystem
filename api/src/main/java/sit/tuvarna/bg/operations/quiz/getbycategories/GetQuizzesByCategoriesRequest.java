package sit.tuvarna.bg.operations.quiz.getbycategories;

import lombok.*;
import sit.tuvarna.bg.base.ProcessorRequest;
import sit.tuvarna.bg.model.QuizCategory;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetQuizzesByCategoriesRequest implements ProcessorRequest {

    private QuizCategory category;
}
