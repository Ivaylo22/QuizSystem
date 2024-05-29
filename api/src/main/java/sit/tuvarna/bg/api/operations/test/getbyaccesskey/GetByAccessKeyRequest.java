package sit.tuvarna.bg.api.operations.test.getbyaccesskey;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetByAccessKeyRequest implements ProcessorRequest {
    private String accessKey;
    private String userEmail;
}
