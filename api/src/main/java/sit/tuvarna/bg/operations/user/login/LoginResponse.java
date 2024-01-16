package sit.tuvarna.bg.operations.user.login;

import lombok.*;
import sit.tuvarna.bg.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginResponse implements ProcessorResponse {

    private String token;
}
