package sit.tuvarna.bg.api.operations.test.deletekey;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteKeyResponse implements ProcessorResponse {
    private String testId;
}
