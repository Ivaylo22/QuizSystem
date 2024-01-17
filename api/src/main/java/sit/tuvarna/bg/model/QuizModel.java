package sit.tuvarna.bg.model;

import lombok.*;
import sit.tuvarna.bg.enums.QuizCategory;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizModel {

    private String name;

    private QuizCategory category;

    private Integer maxExperience;
}
