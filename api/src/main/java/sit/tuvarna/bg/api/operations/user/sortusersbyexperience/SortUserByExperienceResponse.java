package sit.tuvarna.bg.api.operations.user.sortusersbyexperience;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.model.UserModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SortUserByExperienceResponse implements ProcessorResponse {
    private List<UserModel> users;
}
