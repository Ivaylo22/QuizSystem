package sit.tuvarna.bg.api.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerStatisticsModel {

    private String questionId;
    private String answerContent;
    private Long count;
}
