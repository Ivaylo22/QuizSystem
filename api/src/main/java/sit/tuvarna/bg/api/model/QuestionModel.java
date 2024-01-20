package sit.tuvarna.bg.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sit.tuvarna.bg.api.enums.QuestionType;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionModel {

    private String content;

    private QuestionType questionType;

    private boolean openEnded;

    private String correctAnswer;

    private List<AnswerModel> answers;
}
