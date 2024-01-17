package sit.tuvarna.bg.api.operations.quiz.getcompleted;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetCompletedQuizzesRequest implements ProcessorRequest {

    @UUID
    @NotBlank(message = "User id is required")
    private String userId;
}
