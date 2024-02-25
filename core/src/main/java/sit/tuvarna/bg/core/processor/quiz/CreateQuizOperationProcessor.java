package sit.tuvarna.bg.core.processor.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.convert.ConversionService;
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
import sit.tuvarna.bg.persistence.repository.AnswerRepository;
import sit.tuvarna.bg.persistence.repository.CategoryRepository;
import sit.tuvarna.bg.persistence.repository.QuestionRepository;
import sit.tuvarna.bg.persistence.repository.QuizRepository;

import java.util.List;

@Service
public class CreateQuizOperationProcessor implements CreateQuizOperation {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ConversionService conversionService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public CreateQuizOperationProcessor(QuizRepository quizRepository,
                                        QuestionRepository questionRepository,
                                        AnswerRepository answerRepository,
                                        ConversionService conversionService,
                                        CategoryRepository categoryRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.conversionService = conversionService;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CreateQuizResponse process(CreateQuizRequest request) {
        List<Question> questionList = request.getQuestions()
                .stream()
                .map(q -> {
                    Question question = Question.builder()
                            .question(q.getQuestion())
                            .image(q.getImage())
                            .type(QuestionType.valueOf(q.getQuestionType().name()))
                            .answers(q.getAnswers()
                                    .stream()
                                    .map(a -> {
                                        Answer answer = conversionService.convert(a, Answer.class);
                                        try {
                                            answerRepository.save(answer);
                                        }
                                        catch (Exception e) {
                                            throw new DatabaseException();
                                        }
                                        return answer;
                                    }
                            ).toList())
                            .build();

                    try {
                        questionRepository.save(question);
                    } catch (Exception e) {
                        throw new DatabaseException();
                    }
                    return question;
                })
                .toList();

        Category savedCategory = categoryRepository.findByCategory(request.getCategory())
                .orElseGet(() ->
                        categoryRepository.save(Category.builder()
                                .category(request.getCategory())
                                .build()));

        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .category(savedCategory)
                .isActive(true)
                .isRequested(true)
                .questions(questionList)
                .creatorEmail(request.getUserEmail())
                .build();

        try {
            Quiz savedQuiz = quizRepository.save(quiz);
            return CreateQuizResponse.builder()
                    .id(String.valueOf(savedQuiz.getId()))
                    .build();
        } catch (Exception e) {
            throw new DatabaseException();
        }
    }
}
