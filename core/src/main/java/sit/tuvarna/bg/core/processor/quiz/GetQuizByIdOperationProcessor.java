package sit.tuvarna.bg.core.processor.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.InvalidQuizException;
import sit.tuvarna.bg.api.exception.QuizNotFoundException;
import sit.tuvarna.bg.api.model.QuizModel;
import sit.tuvarna.bg.api.operations.quiz.getbyid.GetQuizByIdOperation;
import sit.tuvarna.bg.api.operations.quiz.getbyid.GetQuizByIdRequest;
import sit.tuvarna.bg.api.operations.quiz.getbyid.GetQuizByIdResponse;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.repository.QuizRepository;

import java.util.UUID;

@Service
public class GetQuizByIdOperationProcessor implements GetQuizByIdOperation {
    private final QuizRepository quizRepository;
    private final ConversionService conversionService;

    @Autowired
    public GetQuizByIdOperationProcessor(QuizRepository quizRepository,
                                         ConversionService conversionService) {
        this.quizRepository = quizRepository;
        this.conversionService = conversionService;
    }

    @Override
    public GetQuizByIdResponse process(GetQuizByIdRequest request) {
        Quiz quiz = quizRepository.findById(UUID.fromString(request.getQuizId()))
                .orElseThrow(QuizNotFoundException::new);

        if(!quiz.getIsActive() || quiz.getIsRequested()) {
            throw new InvalidQuizException();
        }

        return GetQuizByIdResponse.builder()
                .quizModel(conversionService.convert(quiz, QuizModel.class))
                .build();
    }
}
