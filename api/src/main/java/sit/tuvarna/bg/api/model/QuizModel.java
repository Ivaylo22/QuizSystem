package sit.tuvarna.bg.api.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizModel {

    private String name;

    private String category;

    private Double averageTime;

    private Double averageCorrectAnswers;

    private Boolean haveBeenCompleted;

    private Boolean haveBeenPassed;
}
