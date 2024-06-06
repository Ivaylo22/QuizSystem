package sit.tuvarna.bg.api.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LeaderboardModel {
    private String userEmail;
    private Integer achievementPoints;
    private Integer level;
    private LocalDateTime registeredAt;
    private Integer solvedQuizzesCount;
}
