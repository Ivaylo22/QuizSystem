package sit.tuvarna.bg.api.operations.user.sortusersbyachievements;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.model.UserModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SortUserByAchievementsResponse implements ProcessorResponse {
    private List<UserModel> users;
}
