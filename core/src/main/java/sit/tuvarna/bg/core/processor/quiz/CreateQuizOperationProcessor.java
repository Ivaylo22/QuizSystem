package sit.tuvarna.bg.core.processor.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.DatabaseException;
import sit.tuvarna.bg.api.operations.quiz.create.CreateQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.create.CreateQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.create.CreateQuizResponse;
import sit.tuvarna.bg.persistence.entity.Answer;
import sit.tuvarna.bg.persistence.entity.Category;
import sit.tuvarna.bg.persistence.entity.Question;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.enums.QuestionType;
import sit.tuvarna.bg.persistence.enums.QuizStatus;
import sit.tuvarna.bg.persistence.repository.CategoryRepository;
import sit.tuvarna.bg.persistence.repository.QuizRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateQuizOperationProcessor implements CreateQuizOperation {
    private final QuizRepository quizRepository;
    private final CategoryRepository categoryRepository;
    private final ConversionService conversionService;

    @Override
    @Transactional
    public CreateQuizResponse process(CreateQuizRequest request) {
        Category category = categoryRepository.findByCategory(request.getCategory())
                .orElseGet(() -> {
                    Category newCategory = Category.builder()
                            .category(request.getCategory())
                            .build();
                    return categoryRepository.save(newCategory);
                });

        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .category(category)
                .status(QuizStatus.REQUESTED)
                .creatorEmail(request.getUserEmail())
                .build();

        List<Question> questions = new ArrayList<>();
        request.getQuestions().forEach(q -> {
            Question question = Question.builder()
                    .question(q.getQuestion())
                    .image(q.getImage())
                    .type(QuestionType.valueOf(q.getQuestionType().name()))
                    .build();

            List<Answer> answers = new ArrayList<>();
            q.getAnswers().forEach(a -> {
                Answer answer = conversionService.convert(a, Answer.class);
                assert answer != null;
                answer.setQuestion(question);
                answers.add(answer);
            });

            question.setAnswers(answers);
            question.setQuiz(quiz);
            questions.add(question);
        });

        quiz.setQuestions(questions);

        try {
            Quiz savedQuiz = quizRepository.save(quiz);
            return CreateQuizResponse.builder()
                    .id(savedQuiz.getId().toString())
                    .questionIds(savedQuiz.getQuestions()
                            .stream()
                            .map(question -> question.getId().toString())
                            .toList())
                    .build();
        } catch (Exception e) {
            throw new DatabaseException();
        }
    }
}
