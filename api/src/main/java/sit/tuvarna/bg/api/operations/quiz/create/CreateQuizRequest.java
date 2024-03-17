package sit.tuvarna.bg.api.operations.quiz.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import sit.tuvarna.bg.api.base.ProcessorRequest;
import sit.tuvarna.bg.api.model.QuestionModel;

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
    private String category;

    @NotBlank(message = "User email is required.")
    private String userEmail;

    @Valid
    @Size(min = 5, message = "Minimum 5 questions.")
    private List<QuestionModel> questions;
}
