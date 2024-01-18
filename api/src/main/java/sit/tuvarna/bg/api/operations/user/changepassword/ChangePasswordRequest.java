package sit.tuvarna.bg.api.operations.user.changepassword;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangePasswordRequest implements ProcessorRequest {

    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "New password is required.")
    private String newPassword;

    @NotBlank(message = "Confirm new password is required.")
    private String confirmNewPassword;
}
