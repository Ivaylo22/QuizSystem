package sit.tuvarna.bg.api.model;

import lombok.*;
import sit.tuvarna.bg.api.enums.QuestionType;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionModel {

    private String question;

    private byte[] image;

    private QuestionType questionType;

    private List<AnswerModel> answers;
}
