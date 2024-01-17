package sit.tuvarna.bg.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerModel {

    @NotBlank(message = "Answer content is required.")
    private String content;

    private Boolean isCorrect;
}
