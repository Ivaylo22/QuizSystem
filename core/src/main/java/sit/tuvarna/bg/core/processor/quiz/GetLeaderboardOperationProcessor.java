package sit.tuvarna.bg.core.processor.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.model.LeaderboardModel;
import sit.tuvarna.bg.api.operations.quiz.leaderboard.GetLeaderboardOperation;
import sit.tuvarna.bg.api.operations.quiz.leaderboard.GetLeaderboardRequest;
import sit.tuvarna.bg.api.operations.quiz.leaderboard.GetLeaderboardResponse;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.repository.UserRepository;
import sit.tuvarna.bg.persistence.repository.UsersQuizzesRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetLeaderboardOperationProcessor implements GetLeaderboardOperation {
    private final UserRepository userRepository;
    private final UsersQuizzesRepository usersQuizzesRepository;

    @Override
    public GetLeaderboardResponse process(GetLeaderboardRequest request) {
        List<User> allUsers = userRepository.findAll();

        List<LeaderboardModel> leaderboardModels = allUsers.stream()
                .map(u -> {
                    int distinctSolvedQuizzesCount = (int) usersQuizzesRepository.getUsersQuizzesByUser(u).stream()
                            .map(usersQuizzes -> usersQuizzes.getQuiz().getId())
                            .distinct()
                            .count();

                    return LeaderboardModel.builder()
                            .userEmail(u.getEmail())
                            .level(u.getLevel())
                            .achievementPoints(u.getAchievementPoints())
                            .solvedQuizzesCount(distinctSolvedQuizzesCount)
                            .registeredAt(u.getCreatedAt())
                            .build();
                })
                .toList();

        return GetLeaderboardResponse.builder()
                .leaderboardModelList(leaderboardModels)
                .build();
    }
}
