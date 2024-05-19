package sit.tuvarna.bg.api.operations.test.getpublictests;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.model.TestModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetPublicTestsResponse implements ProcessorResponse {

    List<TestModel> tests;
}
