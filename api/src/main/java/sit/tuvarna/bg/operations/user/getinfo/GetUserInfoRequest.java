package sit.tuvarna.bg.operations.user.getinfo;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import sit.tuvarna.bg.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetUserInfoRequest implements ProcessorRequest {

    @UUID
    @NotBlank(message = "User id is required")
    private String id;
}
