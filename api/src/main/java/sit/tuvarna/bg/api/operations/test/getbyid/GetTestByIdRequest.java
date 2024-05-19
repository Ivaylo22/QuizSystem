package sit.tuvarna.bg.api.operations.test.getbyid;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import sit.tuvarna.bg.api.base.ProcessorRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetTestByIdRequest implements ProcessorRequest {

    @UUID
    @NotBlank(message = "Test id is required")
    private String testId;
}
