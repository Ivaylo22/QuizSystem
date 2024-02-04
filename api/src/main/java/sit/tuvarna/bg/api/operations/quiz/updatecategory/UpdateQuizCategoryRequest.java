package sit.tuvarna.bg.api.operations.quiz.updatecategory;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import sit.tuvarna.bg.api.base.ProcessorRequest;
import sit.tuvarna.bg.api.enums.QuizCategory;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateQuizCategoryRequest implements ProcessorRequest {

    @UUID
    @NotBlank(message = "ID is required")
    private String id;

    @NotBlank(message = "New category is required")
    private QuizCategory category;
}
