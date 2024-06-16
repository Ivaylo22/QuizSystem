package sit.tuvarna.bg.api.operations.test.archive;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArchiveTestResponse implements ProcessorResponse {
    private String status;
}
