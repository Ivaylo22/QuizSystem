package sit.tuvarna.bg.api.operations.quiz.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.UUID;
import sit.tuvarna.bg.api.base.ProcessorRequest;
import sit.tuvarna.bg.api.model.QuestionModel;
import sit.tuvarna.bg.api.enums.QuizCategory;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateQuizRequest implements ProcessorRequest {

    @NotBlank(message = "Title is required.")
    private String title;

    @NotBlank(message = "Category is required.")
    private QuizCategory category;

    @NotBlank(message = "User email is required.")
    private String userEmail;

    @Size(min = 10, max = 10, message = "Exactly 10 questions are required.")
    @Valid
    private List<QuestionModel> questions;
}
