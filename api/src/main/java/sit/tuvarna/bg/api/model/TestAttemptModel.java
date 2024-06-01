package sit.tuvarna.bg.api.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestAttemptModel {
    private String userId;
    private String testId;
    private String userEmail;
    private String testName;
    private String solvedAt;
    private String grade;
}
