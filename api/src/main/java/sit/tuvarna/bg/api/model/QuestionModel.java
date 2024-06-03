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

    private String id;

    private String question;

    private String image;

    private Integer maximumPoints;

    private Integer earnedPoints;

    private QuestionType questionType;

    private List<String> chosenAnswers;

    private List<AnswerModel> answers;
}
