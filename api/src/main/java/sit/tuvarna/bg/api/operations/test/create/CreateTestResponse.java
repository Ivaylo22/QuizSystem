package sit.tuvarna.bg.api.operations.test.create;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateTestResponse implements ProcessorResponse {

    private String id;
}
