package sit.tuvarna.bg.core.processor.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.QuizNotFoundException;
import sit.tuvarna.bg.api.operations.quiz.approve.ApproveQuizResponse;
import sit.tuvarna.bg.api.operations.quiz.decline.DeclineQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.decline.DeclineQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.decline.DeclineQuizResponse;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.enums.QuizStatus;
import sit.tuvarna.bg.persistence.repository.QuizRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeclineQuizOperationProcessor implements DeclineQuizOperation {

    private final QuizRepository quizRepository;

    @Override
    public DeclineQuizResponse process(DeclineQuizRequest request) {
        Quiz quiz = quizRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(QuizNotFoundException::new);

        quiz.setStatus(QuizStatus.DECLINED);

        Quiz persistedQuiz = quizRepository.save(quiz);

        return DeclineQuizResponse.builder()
                .status(persistedQuiz.getStatus().name())
                .build();
    }
}
