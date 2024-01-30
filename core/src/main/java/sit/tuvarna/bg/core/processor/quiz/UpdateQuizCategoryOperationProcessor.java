package sit.tuvarna.bg.core.processor.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.QuizNotFoundException;
import sit.tuvarna.bg.api.operations.quiz.updatecategory.UpdateQuizCategoryOperation;
import sit.tuvarna.bg.api.operations.quiz.updatecategory.UpdateQuizCategoryRequest;
import sit.tuvarna.bg.api.operations.quiz.updatecategory.UpdateQuizCategoryResponse;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.enums.QuizCategory;
import sit.tuvarna.bg.persistence.repository.QuizRepository;

import java.util.UUID;

@Service
public class UpdateQuizCategoryOperationProcessor implements UpdateQuizCategoryOperation {

    private final QuizRepository quizRepository;
    @Autowired
    public UpdateQuizCategoryOperationProcessor(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Override
    public UpdateQuizCategoryResponse process(UpdateQuizCategoryRequest request) {
        Quiz quiz = quizRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(QuizNotFoundException::new);

        quiz.setCategory(QuizCategory.valueOf(request.getCategory().name()));
        quizRepository.save(quiz);

        return UpdateQuizCategoryResponse.builder()
                .category(request.getCategory())
                .build();
    }
}
