package sit.tuvarna.bg.api.operations.quiz.getrequested;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetRequestedQuizzesRequest implements ProcessorRequest {
    private String userEmail;
}
