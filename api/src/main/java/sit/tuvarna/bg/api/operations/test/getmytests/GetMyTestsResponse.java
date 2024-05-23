package sit.tuvarna.bg.api.operations.test.getmytests;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.model.TestModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMyTestsResponse implements ProcessorResponse {

    private List<TestModel> tests;
}
