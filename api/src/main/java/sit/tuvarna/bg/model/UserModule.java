package sit.tuvarna.bg.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserModule {

    private String username;

    private String email;

    private String password;

    private String avatarUrl;

    private Integer level;

    private Integer experience;

    private Integer achievementPoints;
}
