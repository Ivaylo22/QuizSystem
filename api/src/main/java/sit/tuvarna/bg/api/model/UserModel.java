package sit.tuvarna.bg.api.model;

import lombok.*;
import sit.tuvarna.bg.api.enums.Role;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserModel {

    private String email;

    private String avatarUrl;

    private Integer level;

    private Integer experience;

    private Integer achievementPoints;

    private Boolean isArchived;

    private Integer quizzesUnderOneMinuteCount;

    private Integer perfectQuizzesCount;

    private Integer consecutiveQuizzesPassedCount;

    private Integer consecutiveDailyQuizzesCount;

    private Integer dailyQuizzesCount;

    private Integer quizzesPassedCount;

    private Role role;

    private LocalDateTime createdAt;
}
