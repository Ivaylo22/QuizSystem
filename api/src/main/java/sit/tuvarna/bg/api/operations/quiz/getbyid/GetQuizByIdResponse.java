package sit.tuvarna.bg.api.operations.quiz.getbyid;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.model.QuestionModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetQuizByIdResponse implements ProcessorResponse {

    private String title;

    private String category;

    private String userEmail;

    private Boolean isDaily;

    private List<QuestionModel> questions;
}
