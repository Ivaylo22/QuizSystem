package sit.tuvarna.bg.api.operations.user.archive;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArchiveUserRequest implements ProcessorRequest {
    private String email;
}
