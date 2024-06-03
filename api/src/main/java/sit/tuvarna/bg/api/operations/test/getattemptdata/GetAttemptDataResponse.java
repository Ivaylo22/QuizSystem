package sit.tuvarna.bg.api.operations.test.getattemptdata;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.model.QuestionModel;
import sit.tuvarna.bg.api.model.TestAttemptModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetAttemptDataResponse implements ProcessorResponse {
    private TestAttemptModel testAttempt;
    private List<QuestionModel> questions;
}
