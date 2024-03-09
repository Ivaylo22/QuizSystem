package sit.tuvarna.bg.core.processor.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.QuizAlreadyArchivedException;
import sit.tuvarna.bg.api.exception.QuizNotFoundException;
import sit.tuvarna.bg.api.operations.quiz.archive.ArchiveQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.archive.ArchiveQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.archive.ArchiveQuizResponse;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.enums.QuizStatus;
import sit.tuvarna.bg.persistence.repository.QuizRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArchiveQuizOperationProcessor implements ArchiveQuizOperation {

    private final QuizRepository quizRepository;

    @Override
    public ArchiveQuizResponse process(ArchiveQuizRequest request) {
        Quiz quiz = quizRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(QuizNotFoundException::new);

        if (quiz.getStatus() == QuizStatus.ARCHIVED) {
            throw new QuizAlreadyArchivedException();
        }

        quiz.setStatus(QuizStatus.ARCHIVED);
        quizRepository.save(quiz);

        return ArchiveQuizResponse
                .builder()
                .status(quiz.getStatus().name())
                .build();
    }
}
