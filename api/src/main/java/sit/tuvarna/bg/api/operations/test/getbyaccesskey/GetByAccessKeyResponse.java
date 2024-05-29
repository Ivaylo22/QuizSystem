package sit.tuvarna.bg.api.operations.test.getbyaccesskey;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.model.SectionModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetByAccessKeyResponse implements ProcessorResponse {

    private String id;

    private String title;

    private Integer grade;

    private String subject;

    private String creatorEmail;

    private Integer minutesToSolve;

    private Boolean mixedQuestions;

    private List<SectionModel> sections;

    private String status;

    private Boolean alreadySolved;
}
