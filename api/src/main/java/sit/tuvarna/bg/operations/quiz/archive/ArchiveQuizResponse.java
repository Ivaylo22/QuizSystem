package sit.tuvarna.bg.operations.quiz.archive;

import lombok.*;
import sit.tuvarna.bg.base.ProcessorResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArchiveQuizResponse implements ProcessorResponse {

    private String isArchived;
}
