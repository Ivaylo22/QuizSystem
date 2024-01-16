package sit.tuvarna.bg.operations.user.getinfo;

import lombok.*;
import sit.tuvarna.bg.base.ProcessorResponse;
import sit.tuvarna.bg.model.UserModule;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetUserInfoResponse implements ProcessorResponse {

    private UserModule userInformation;
}
