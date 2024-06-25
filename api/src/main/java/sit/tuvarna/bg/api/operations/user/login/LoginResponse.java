package sit.tuvarna.bg.api.operations.user.login;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse implements ProcessorResponse {

    private String token;

    private String email;
}
