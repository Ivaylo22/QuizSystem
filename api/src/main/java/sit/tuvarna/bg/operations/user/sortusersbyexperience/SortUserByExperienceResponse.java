package sit.tuvarna.bg.operations.user.sortusersbyexperience;

import lombok.*;
import sit.tuvarna.bg.base.ProcessorResponse;
import sit.tuvarna.bg.model.UserModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SortUserByExperienceResponse implements ProcessorResponse {
    private List<UserModel> users;
}
