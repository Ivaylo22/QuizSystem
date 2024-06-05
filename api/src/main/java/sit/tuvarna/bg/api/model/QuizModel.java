package sit.tuvarna.bg.api.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizModel {

    private String quizId;

    private String name;

    private String category;

    private Integer questionsCount;

    private Integer averageSecondsNeeded;

    private Double averageCorrectAnswers;

    private Boolean haveBeenCompleted;

    private Boolean haveBeenPassed;

    private String userEmail;

    private Integer personalBestTime;

    private Integer personalBestCorrectAnswers;

    private Integer personalBestXpGained;

    private LocalDateTime createdAt;

    private Integer attemptsCount;
}
