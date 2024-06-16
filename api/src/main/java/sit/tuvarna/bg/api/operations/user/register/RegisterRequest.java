package sit.tuvarna.bg.api.operations.user.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisterRequest implements ProcessorRequest {
    @Email(message = "Invalid email format.", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#,])[A-Za-z\\d@$!%*?&#,]{8,}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character from @$!%*?&#,")
    private String password;

    @NotBlank(message = "Confirm password is required.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#,])[A-Za-z\\d@$!%*?&#,]{8,}$", message = "Confirm password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character from @$!%*?&#,")
    private String confirmPassword;
}
