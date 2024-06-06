package sit.tuvarna.bg.api.operations.quiz.leaderboard;

import lombok.*;
import sit.tuvarna.bg.api.base.ProcessorResponse;
import sit.tuvarna.bg.api.model.LeaderboardModel;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetLeaderboardResponse implements ProcessorResponse {

    List<LeaderboardModel> leaderboardModelList;
}
