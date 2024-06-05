package sit.tuvarna.bg.core.processor.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.model.QuizModel;
import sit.tuvarna.bg.api.operations.quiz.getmyquizzes.GetMyQuizzesOperation;
import sit.tuvarna.bg.api.operations.quiz.getmyquizzes.GetMyQuizzesRequest;
import sit.tuvarna.bg.api.operations.quiz.getmyquizzes.GetMyQuizzesResponse;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.repository.QuizRepository;
import sit.tuvarna.bg.persistence.repository.UsersQuizzesRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMyQuizzesOperationProcessor implements GetMyQuizzesOperation {

    private final QuizRepository quizRepository;
    private final UsersQuizzesRepository usersQuizzesRepository;

    @Override
    public GetMyQuizzesResponse process(GetMyQuizzesRequest request) {
        List<Quiz> quizzes = quizRepository.getAllByCreatorEmail(request.getEmail());

        List<QuizModel> quizModels = quizzes.stream()
                .map(q -> QuizModel.builder()
                        .quizId(q.getId().toString())
                        .name(q.getTitle())
                        .category(q.getCategory().getCategory())
                        .createdAt(q.getCreatedAt())
                        .attemptsCount(usersQuizzesRepository.getUsersQuizzesByQuiz(q).size())
                        .status(q.getStatus().name())
                        .build())
                .toList();

        return GetMyQuizzesResponse.builder()
                .quizModelList(quizModels)
                .build();
    }
}
