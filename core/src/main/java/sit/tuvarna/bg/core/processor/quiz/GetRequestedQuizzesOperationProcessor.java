package sit.tuvarna.bg.core.processor.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.model.QuizModel;
import sit.tuvarna.bg.api.operations.quiz.getrequested.GetRequestedQuizzesOperation;
import sit.tuvarna.bg.api.operations.quiz.getrequested.GetRequestedQuizzesRequest;
import sit.tuvarna.bg.api.operations.quiz.getrequested.GetRequestedQuizzesResponse;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.repository.QuizRepository;

import java.util.List;

@Service
public class GetRequestedQuizzesOperationProcessor implements GetRequestedQuizzesOperation {
    private final QuizRepository quizRepository;
    private final ConversionService conversionService;

    @Autowired
    public GetRequestedQuizzesOperationProcessor(QuizRepository quizRepository,
                                                 ConversionService conversionService) {
        this.quizRepository = quizRepository;
        this.conversionService = conversionService;
    }

    @Override
    public GetRequestedQuizzesResponse process(GetRequestedQuizzesRequest request) {
        List<Quiz> quizzes = quizRepository.findAllByIsRequestedIsTrue();

        return GetRequestedQuizzesResponse.builder()
                .quizzes(quizzes
                        .stream()
                        .map(q -> conversionService.convert(q, QuizModel.class))
                        .toList())
                .build();
    }
}
