package sit.tuvarna.bg.api.operations.user.register;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisterResponse implements ProcessorResponse {

    private String username;

    private String email;

    private String phoneNumber;
}
