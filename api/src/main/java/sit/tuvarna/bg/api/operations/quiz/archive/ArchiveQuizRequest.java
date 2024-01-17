package sit.tuvarna.bg.api.operations.quiz.archive;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArchiveQuizRequest implements ProcessorRequest {

    @UUID
    @NotBlank(message = "ID is required")
    private String id;
}
