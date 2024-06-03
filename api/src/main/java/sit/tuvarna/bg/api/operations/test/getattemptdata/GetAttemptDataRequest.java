package sit.tuvarna.bg.api.operations.test.getattemptdata;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetAttemptDataRequest implements ProcessorRequest {
    private String testId;
    private String userEmail;
}
