package sit.tuvarna.bg.api.operations.quiz.getmyquizzes;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMyQuizzesRequest implements ProcessorRequest {

    private String email;
}
