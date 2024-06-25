package sit.tuvarna.bg.api.operations.test.solve;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolveTestResponse implements ProcessorResponse {

    private Double grade;
}
