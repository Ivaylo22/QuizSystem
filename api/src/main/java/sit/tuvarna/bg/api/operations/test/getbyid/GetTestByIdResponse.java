package sit.tuvarna.bg.api.operations.test.getbyid;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.model.SectionModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTestByIdResponse implements ProcessorResponse {

    private String id;

    private String title;

    private Integer grade;

    private String subject;

    private String creatorEmail;

    private Integer minutesToSolve;

    private Boolean mixedQuestions;

    private List<SectionModel> sections;

    private String status;
}
