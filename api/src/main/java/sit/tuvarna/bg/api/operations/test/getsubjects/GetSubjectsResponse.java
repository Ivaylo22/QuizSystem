package sit.tuvarna.bg.api.operations.test.getsubjects;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetSubjectsResponse implements ProcessorResponse {

    List<String> subjects;
}
