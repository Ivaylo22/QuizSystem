package sit.tuvarna.bg.api.operations.test.getmytests;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMyTestsRequest implements ProcessorRequest {

    private String email;
}
