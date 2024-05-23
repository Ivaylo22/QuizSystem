package sit.tuvarna.bg.api.model;

import lombok.*;
import sit.tuvarna.bg.api.enums.TestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestModel {
    private String id;
    private String title;
    private Integer grade;
    private String subject;
    private String creatorEmail;
    private Integer minutesToSolve;
    private LocalDateTime createdAt;
    private Integer attemptsCount;
    private TestStatus status;
}
