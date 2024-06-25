package sit.tuvarna.bg.api.operations.quiz.getcategories;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCategoriesResponse implements ProcessorResponse {

    private List<String> categories;
}
