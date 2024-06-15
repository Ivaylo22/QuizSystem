package sit.tuvarna.bg.api.operations.test.getrandom;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetRandomRequest implements ProcessorRequest {
    private String grade;
    private String subject;
}
