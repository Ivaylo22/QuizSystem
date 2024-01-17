package sit.tuvarna.bg.operations.quiz.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import sit.tuvarna.bg.base.ProcessorRequest;
import sit.tuvarna.bg.model.QuestionModel;
import sit.tuvarna.bg.enums.QuizCategory;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateQuizRequest implements ProcessorRequest {

    @NotBlank(message = "Title is required.")
    private String title;

    private QuizCategory category;

    private Integer maxExperience;

    @Size(min = 10, max = 10, message = "Exactly 10 questions are required.")
    @Valid
    private List<QuestionModel> questions;
}
