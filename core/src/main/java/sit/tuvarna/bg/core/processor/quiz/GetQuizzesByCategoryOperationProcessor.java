package sit.tuvarna.bg.core.processor.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.model.QuizModel;
import sit.tuvarna.bg.api.operations.quiz.getbycategory.GetQuizzesByCategoryOperation;
import sit.tuvarna.bg.api.operations.quiz.getbycategory.GetQuizzesByCategoryRequest;
import sit.tuvarna.bg.api.operations.quiz.getbycategory.GetQuizzesByCategoryResponse;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.enums.QuizCategory;
import sit.tuvarna.bg.persistence.repository.QuizRepository;

import java.util.List;

@Service
public class GetQuizzesByCategoryOperationProcessor implements GetQuizzesByCategoryOperation {
    private final QuizRepository quizRepository;
    private final ConversionService conversionService;

    @Autowired
    public GetQuizzesByCategoryOperationProcessor(QuizRepository quizRepository,
                                                  ConversionService conversionService) {
        this.quizRepository = quizRepository;
        this.conversionService = conversionService;
    }

    @Override
    public GetQuizzesByCategoryResponse process(GetQuizzesByCategoryRequest request) {
        List<Quiz> quizzes = quizRepository
                .findAllByCategory(QuizCategory.valueOf(request.getCategory().name()));

        List<QuizModel> quizModels = quizzes
                .stream()
                .map(q -> conversionService.convert(q, QuizModel.class))
                .toList();

        return GetQuizzesByCategoryResponse.builder()
                .quizzes(quizModels)
                .build();
    }
}
