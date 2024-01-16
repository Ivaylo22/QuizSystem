package sit.tuvarna.bg.operations.quiz.archive;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import sit.tuvarna.bg.base.ProcessorRequest;

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
