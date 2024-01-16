package sit.tuvarna.bg.operations.user.changepassword;

import lombok.*;
import sit.tuvarna.bg.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangePasswordResponse implements ProcessorResponse {

    private String email;

    private String newPassword;
}
