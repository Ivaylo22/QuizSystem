package sit.tuvarna.bg.api.operations.test.gettestattempts;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.model.TestAttemptModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetTestAttemptsResponse implements ProcessorResponse {
    List<TestAttemptModel> attempts;
}
