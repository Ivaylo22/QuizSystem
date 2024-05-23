package sit.tuvarna.bg.api.operations.test.deletekey;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteKeyRequest implements ProcessorRequest {
    private String testId;
}
