package sit.tuvarna.bg.api.operations.test.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorRequest;
import sit.tuvarna.bg.api.enums.TestStatus;
import sit.tuvarna.bg.api.model.SectionModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateTestRequest implements ProcessorRequest {

    @NotBlank(message = "Title is required.")
    private String title;

    private Integer grade;

    @NotBlank(message = "Subject is required.")
    private String subject;

    @NotBlank(message = "Creator email is required.")
    private String creatorEmail;

    private Boolean hasMixedQuestions;

    @Valid
    private List<SectionModel> sections;

    private Integer minutesToSolve;

    private TestStatus status;

    private String scoringFormula;
}
