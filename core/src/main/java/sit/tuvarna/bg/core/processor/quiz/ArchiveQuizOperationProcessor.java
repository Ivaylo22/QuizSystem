package sit.tuvarna.bg.core.processor.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.QuizAlreadyActiveException;
import sit.tuvarna.bg.api.exception.QuizAlreadyArchivedException;
import sit.tuvarna.bg.api.exception.QuizNotFoundException;
import sit.tuvarna.bg.api.operations.quiz.active.ActiveQuizResponse;
import sit.tuvarna.bg.api.operations.quiz.archive.ArchiveQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.archive.ArchiveQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.archive.ArchiveQuizResponse;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.repository.QuizRepository;

import java.util.UUID;

@Service
public class ArchiveQuizOperationProcessor implements ArchiveQuizOperation {

    private final QuizRepository quizRepository;

    @Autowired
    public ArchiveQuizOperationProcessor(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Override
    public ArchiveQuizResponse process(ArchiveQuizRequest request) {
        Quiz quiz = quizRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(QuizNotFoundException::new);

        if(!quiz.getIsActive()) {
            throw new QuizAlreadyArchivedException();
        }

        quiz.setIsActive(false);
        quizRepository.save(quiz);

        return ArchiveQuizResponse
                .builder()
                .isArchived(quiz.getIsActive())
                .build();
    }
}
