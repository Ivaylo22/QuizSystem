package sit.tuvarna.bg.core.processor.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.QuizNotFoundException;
import sit.tuvarna.bg.api.operations.quiz.approve.ApproveQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.approve.ApproveQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.approve.ApproveQuizResponse;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.enums.QuizStatus;
import sit.tuvarna.bg.persistence.repository.QuizRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApproveQuizOperationProcessor implements ApproveQuizOperation {
    private final QuizRepository quizRepository;

    @Override
    public ApproveQuizResponse process(ApproveQuizRequest request) {
        Quiz quiz = quizRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(QuizNotFoundException::new);

        quiz.setStatus(QuizStatus.ACTIVE);

        Quiz persistedQuiz = quizRepository.save(quiz);

        return ApproveQuizResponse.builder()
                .status(persistedQuiz.getStatus().name())
                .build();
    }
}
