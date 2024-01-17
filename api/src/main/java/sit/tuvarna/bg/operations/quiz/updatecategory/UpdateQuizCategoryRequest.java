package sit.tuvarna.bg.operations.quiz.updatecategory;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import sit.tuvarna.bg.base.ProcessorRequest;
import sit.tuvarna.bg.enums.QuizCategory;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateQuizCategoryRequest implements ProcessorRequest {

    @UUID
    @NotBlank(message = "ID is required")
    private String id;

    private QuizCategory category;
}
