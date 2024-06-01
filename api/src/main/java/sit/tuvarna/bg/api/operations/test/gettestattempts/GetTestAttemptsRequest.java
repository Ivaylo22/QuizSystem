package sit.tuvarna.bg.api.operations.test.gettestattempts;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetTestAttemptsRequest implements ProcessorRequest {
    private String testId;
}
