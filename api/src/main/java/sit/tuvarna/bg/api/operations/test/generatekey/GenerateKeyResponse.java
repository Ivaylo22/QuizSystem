package sit.tuvarna.bg.api.operations.test.generatekey;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenerateKeyResponse implements ProcessorResponse {
    private String accessKey;
}
