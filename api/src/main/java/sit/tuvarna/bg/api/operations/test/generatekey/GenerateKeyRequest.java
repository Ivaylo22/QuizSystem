package sit.tuvarna.bg.api.operations.test.generatekey;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GenerateKeyRequest implements ProcessorRequest {
    private String testId;
}
