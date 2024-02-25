package sit.tuvarna.bg.core.processor.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.QuizNotFoundException;
import sit.tuvarna.bg.api.operations.quiz.updatecategory.UpdateQuizCategoryOperation;
import sit.tuvarna.bg.api.operations.quiz.updatecategory.UpdateQuizCategoryRequest;
import sit.tuvarna.bg.api.operations.quiz.updatecategory.UpdateQuizCategoryResponse;
import sit.tuvarna.bg.persistence.entity.Category;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.repository.CategoryRepository;
import sit.tuvarna.bg.persistence.repository.QuizRepository;

import java.util.UUID;

@Service
public class UpdateQuizCategoryOperationProcessor implements UpdateQuizCategoryOperation {

    private final QuizRepository quizRepository;
    private final CategoryRepository categoryRepository;
    @Autowired
    public UpdateQuizCategoryOperationProcessor(QuizRepository quizRepository,
                                                CategoryRepository categoryRepository) {
        this.quizRepository = quizRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public UpdateQuizCategoryResponse process(UpdateQuizCategoryRequest request) {
        Quiz quiz = quizRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(QuizNotFoundException::new);

        Category savedCategory = categoryRepository.findByCategory(request.getCategory())
                .orElseGet(() ->
                        categoryRepository.save(Category.builder()
                                .category(request.getCategory())
                                .build()));

        quiz.setCategory(savedCategory);
        quizRepository.save(quiz);

        return UpdateQuizCategoryResponse.builder()
                .category(request.getCategory())
                .build();
    }
}
