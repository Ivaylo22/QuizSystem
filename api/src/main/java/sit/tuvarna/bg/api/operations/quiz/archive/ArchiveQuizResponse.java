package sit.tuvarna.bg.api.operations.quiz.archive;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveQuizResponse implements ProcessorResponse {

    private String status;
}
