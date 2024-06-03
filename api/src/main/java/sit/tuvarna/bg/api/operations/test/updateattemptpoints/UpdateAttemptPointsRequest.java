package sit.tuvarna.bg.api.operations.test.updateattemptpoints;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateAttemptPointsRequest implements ProcessorRequest {
    private String testId;
    private String userEmail;
    private Map<String, Double> points;
}
