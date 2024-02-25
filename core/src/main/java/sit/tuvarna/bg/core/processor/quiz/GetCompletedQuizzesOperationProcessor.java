package sit.tuvarna.bg.core.processor.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.api.model.QuizModel;
import sit.tuvarna.bg.api.operations.quiz.getcompleted.GetCompletedQuizzesOperation;
import sit.tuvarna.bg.api.operations.quiz.getcompleted.GetCompletedQuizzesRequest;
import sit.tuvarna.bg.api.operations.quiz.getcompleted.GetCompletedQuizzesResponse;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.entity.UsersQuizzes;
import sit.tuvarna.bg.persistence.repository.UserRepository;
import sit.tuvarna.bg.persistence.repository.UsersQuizzesRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetCompletedQuizzesOperationProcessor implements GetCompletedQuizzesOperation {

    private final UsersQuizzesRepository usersQuizzesRepository;
    private final UserRepository userRepository;
    private final ConversionService conversionService;

    @Override
    public GetCompletedQuizzesResponse process(GetCompletedQuizzesRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);

        List<UsersQuizzes> usersQuizzesList = usersQuizzesRepository.getUsersQuizzesByUser(user);

        List<Quiz> completedQuizzes = usersQuizzesList
                .stream()
                .filter(UsersQuizzes::getIsTaken)
                .map(UsersQuizzes::getQuiz)
                .toList();

        List<QuizModel> quizModels = completedQuizzes
                .stream()
                .filter(Quiz::getIsActive)
                .filter(q -> !q.getIsRequested())
                .map(q -> conversionService.convert(q, QuizModel.class))
                .toList();

        return GetCompletedQuizzesResponse
                .builder()
                .quizzes(quizModels)
                .build();
    }
}
