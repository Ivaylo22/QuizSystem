package sit.tuvarna.bg.api.operations.quiz.getbycategory;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;
import sit.tuvarna.bg.api.enums.QuizCategory;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetQuizzesByCategoryRequest implements ProcessorRequest {

    @NotBlank(message = "Category is required")
    private QuizCategory category;
}
