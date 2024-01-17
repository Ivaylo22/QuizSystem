package sit.tuvarna.bg.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sit.tuvarna.bg.enums.QuestionType;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionModel {

    @NotBlank(message = "Content is required.")
    private String content;

    @NotNull(message = "Question type is required.")
    private QuestionType questionType;

    private boolean openEnded;

    @NotBlank(message = "Correct answer is required.")
    private String correctAnswer;

    @Singular
    private List<AnswerModel> answers;
}
