package sit.tuvarna.bg.api.operations.user.getinfo;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.model.UserModel;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetUserInfoResponse implements ProcessorResponse {

    private UserModel userInformation;
}
