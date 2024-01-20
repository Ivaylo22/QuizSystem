package sit.tuvarna.bg.api.operations.user.archive;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArchiveUserRequest implements ProcessorRequest {

    @NotBlank(message = "User email is required")
    private String email;
}
