package sit.tuvarna.bg.api.operations.test.gettestsummary;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.model.TestSummaryModel;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetTestSummaryResponse implements ProcessorResponse {
    private TestSummaryModel model;
}
