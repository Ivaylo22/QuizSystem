package sit.tuvarna.bg.api.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizModel {

    private String quizId;

    private String name;

    private String category;

    private Integer averageSecondsNeeded;

    private Double averageCorrectAnswers;

    private Boolean haveBeenCompleted;

    private Boolean haveBeenPassed;

    private String userEmail;
}
