package sit.tuvarna.bg.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerModel {

    private String id;

    private String content;

    private Boolean isCorrect;

    private Long count;

    private Double percentage;
}
