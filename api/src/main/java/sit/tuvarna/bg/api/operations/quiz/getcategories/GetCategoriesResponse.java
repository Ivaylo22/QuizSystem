package sit.tuvarna.bg.api.operations.quiz.getcategories;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.model.QuizModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetCategoriesResponse implements ProcessorResponse {

    private List<String> categories;
}