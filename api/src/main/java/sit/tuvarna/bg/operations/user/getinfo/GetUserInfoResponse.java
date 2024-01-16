package sit.tuvarna.bg.operations.user.getinfo;

import lombok.*;
import sit.tuvarna.bg.base.ProcessorResponse;
import sit.tuvarna.bg.model.UserModel;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetUserInfoResponse implements ProcessorResponse {

    private UserModel userInformation;
}
