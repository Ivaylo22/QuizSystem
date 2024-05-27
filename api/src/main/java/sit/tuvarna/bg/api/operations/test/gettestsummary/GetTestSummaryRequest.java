package sit.tuvarna.bg.api.operations.test.gettestsummary;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetTestSummaryRequest implements ProcessorRequest {

    private String testId;
}
