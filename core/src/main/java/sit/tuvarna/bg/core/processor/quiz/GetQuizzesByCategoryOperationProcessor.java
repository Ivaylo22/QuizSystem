package sit.tuvarna.bg.core.processor.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.CategoryNotFoundException;
import sit.tuvarna.bg.api.model.QuizModel;
import sit.tuvarna.bg.api.operations.quiz.getbycategory.GetQuizzesByCategoryOperation;
import sit.tuvarna.bg.api.operations.quiz.getbycategory.GetQuizzesByCategoryRequest;
import sit.tuvarna.bg.api.operations.quiz.getbycategory.GetQuizzesByCategoryResponse;
import sit.tuvarna.bg.persistence.entity.Category;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.repository.CategoryRepository;
import sit.tuvarna.bg.persistence.repository.QuizRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetQuizzesByCategoryOperationProcessor implements GetQuizzesByCategoryOperation {
    private final QuizRepository quizRepository;
    private final ConversionService conversionService;
    private final CategoryRepository categoryRepository;

    @Override
    public GetQuizzesByCategoryResponse process(GetQuizzesByCategoryRequest request) {
        Category savedCategory = categoryRepository.findByCategory(request.getCategory())
                .orElseThrow(CategoryNotFoundException::new);

        List<Quiz> quizzes = quizRepository
                .findAllByCategory(savedCategory);

        List<QuizModel> quizModels = quizzes
                .stream()
                .filter(Quiz::getIsActive)
                .filter(q -> !q.getIsRequested())
                .map(q -> conversionService.convert(q, QuizModel.class))
                .toList();

        return GetQuizzesByCategoryResponse.builder()
                .quizzes(quizModels)
                .build();
    }
}
