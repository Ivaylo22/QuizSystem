package sit.tuvarna.bg.api.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestSummaryModel {

    private String testId;
    private String title;
    private List<QuestionModel> questions;
    private Double averageScore;
}
