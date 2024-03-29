package sit.tuvarna.bg.core.processor.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.QuizAlreadyActiveException;
import sit.tuvarna.bg.api.exception.QuizNotFoundException;
import sit.tuvarna.bg.api.operations.quiz.active.ActiveQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.active.ActiveQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.active.ActiveQuizResponse;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.enums.QuizStatus;
import sit.tuvarna.bg.persistence.repository.QuizRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActiveQuizOperationProcessor implements ActiveQuizOperation {
    private final QuizRepository quizRepository;

    @Override
    public ActiveQuizResponse process(ActiveQuizRequest request) {
        Quiz quiz = quizRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(QuizNotFoundException::new);

        if (quiz.getStatus() == QuizStatus.ACTIVE) {
            throw new QuizAlreadyActiveException();
        }

        quiz.setStatus(QuizStatus.ACTIVE);
        quizRepository.save(quiz);

        return ActiveQuizResponse
                .builder()
                .status(quiz.getStatus().name())
                .build();
    }
}
