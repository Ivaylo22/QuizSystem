package sit.tuvarna.bg.operations.achievement.list;

import lombok.*;
import org.hibernate.validator.constraints.UUID;
import sit.tuvarna.bg.base.ProcessorRequest;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ListAchievementsRequest implements ProcessorRequest {
}
