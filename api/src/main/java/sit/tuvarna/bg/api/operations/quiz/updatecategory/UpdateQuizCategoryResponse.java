package sit.tuvarna.bg.api.operations.quiz.updatecategory;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateQuizCategoryResponse implements ProcessorResponse {

    private String category;
}
