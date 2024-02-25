package sit.tuvarna.bg.core.processor.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.api.model.QuizModel;
import sit.tuvarna.bg.api.operations.quiz.getunfinished.GetUnfinishedQuizzesOperation;
import sit.tuvarna.bg.api.operations.quiz.getunfinished.GetUnfinishedQuizzesRequest;
import sit.tuvarna.bg.api.operations.quiz.getunfinished.GetUnfinishedQuizzesResponse;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.entity.UsersQuizzes;
import sit.tuvarna.bg.persistence.repository.QuizRepository;
import sit.tuvarna.bg.persistence.repository.UserRepository;
import sit.tuvarna.bg.persistence.repository.UsersQuizzesRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUnfinishedQuizzesOperationProcessor implements GetUnfinishedQuizzesOperation {
    private final UsersQuizzesRepository usersQuizzesRepository;
    private final QuizRepository quizRepository;

    private final UserRepository userRepository;
    private final ConversionService conversionService;

    @Override
    public GetUnfinishedQuizzesResponse process(GetUnfinishedQuizzesRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);

        List<UsersQuizzes> usersQuizzesList = usersQuizzesRepository.getUsersQuizzesByUser(user);
        List<Quiz> takenQuizzes = usersQuizzesList.stream()
                .filter(UsersQuizzes::getIsTaken)
                .map(UsersQuizzes::getQuiz)
                .toList();

        List<Quiz> allQuizzes = quizRepository.findAll();

        List<Quiz> notTakenQuizzes = allQuizzes.stream()
                .filter(quiz -> !takenQuizzes.contains(quiz))
                .toList();

        List<QuizModel> quizModels = notTakenQuizzes.stream()
                .filter(Quiz::getIsActive)
                .filter(q -> !q.getIsRequested())
                .map(q -> conversionService.convert(q, QuizModel.class))
                .toList();

        return GetUnfinishedQuizzesResponse.builder()
                .quizzes(quizModels)
                .build();
    }
}
