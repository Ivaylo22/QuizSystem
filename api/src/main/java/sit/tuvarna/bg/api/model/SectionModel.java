package sit.tuvarna.bg.api.model;

import jakarta.validation.Valid;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SectionModel {

    private String id;

    private Integer totalQuestionsCount;

    private Integer usedQuestionsCount;

    @Valid
    private List<QuestionModel> questions;
}
