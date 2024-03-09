package sit.tuvarna.bg.core.processor.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.api.model.QuizModel;
import sit.tuvarna.bg.api.operations.quiz.getallforuser.GetAllQuizzesForUserOperation;
import sit.tuvarna.bg.api.operations.quiz.getallforuser.GetAllQuizzesForUserRequest;
import sit.tuvarna.bg.api.operations.quiz.getallforuser.GetAllQuizzesForUserResponse;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.entity.UsersQuizzes;
import sit.tuvarna.bg.persistence.enums.QuizStatus;
import sit.tuvarna.bg.persistence.repository.QuizRepository;
import sit.tuvarna.bg.persistence.repository.UserRepository;
import sit.tuvarna.bg.persistence.repository.UsersQuizzesRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllQuizzesForUserForUserOperationProcessor implements GetAllQuizzesForUserOperation {

    private final UsersQuizzesRepository usersQuizzesRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    @Override
    public GetAllQuizzesForUserResponse process(GetAllQuizzesForUserRequest request) {
        List<Quiz> quizzes = quizRepository.findAll()
                .stream()
                .filter(q -> q.getStatus() == QuizStatus.ACTIVE)
                .toList();
        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(UserNotFoundException::new);
        List<QuizModel> quizModels = new ArrayList<>();

        if (!quizzes.isEmpty()) {
            for (Quiz quiz : quizzes) {
                List<UsersQuizzes> userQuizzes = usersQuizzesRepository.getUsersQuizzesByUserAndQuiz(user, quiz);

                boolean haveBeenCompleted = !userQuizzes.isEmpty();
                boolean haveBeenPassed = userQuizzes.stream().anyMatch(uq -> uq.getCorrectAnswers() >= 8);

                QuizModel quizModel = QuizModel.builder()
                        .name(quiz.getTitle())
                        .category(quiz.getCategory().getCategory())
                        .averageSecondsNeeded(quiz.getAverageSecondsNeeded())
                        .averageCorrectAnswers(quiz.getAverageCorrectAnswers())
                        .haveBeenCompleted(haveBeenCompleted)
                        .haveBeenPassed(haveBeenPassed)
                        .build();

                quizModels.add(quizModel);
            }
        }

        return GetAllQuizzesForUserResponse.builder()
                .quizModels(quizModels)
                .build();
    }
}
