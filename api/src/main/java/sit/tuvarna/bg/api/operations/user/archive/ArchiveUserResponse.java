package sit.tuvarna.bg.api.operations.user.archive;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveUserResponse implements ProcessorResponse {
    private String email;
    private Boolean isArchived;
}
