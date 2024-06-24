package sit.tuvarna.bg.api.operations.user.register;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse implements ProcessorResponse {

    private String userId;

    private String email;
}
