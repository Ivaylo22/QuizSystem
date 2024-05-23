package sit.tuvarna.bg.api.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionAttemptModel {

    private String questionId;
    private List<String> answers;
    private Double pointsAwarded;
}
