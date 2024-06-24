package sit.tuvarna.bg.api.operations.user.changepassword;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordResponse implements ProcessorResponse {

    private String email;

    private String newPassword;
}
