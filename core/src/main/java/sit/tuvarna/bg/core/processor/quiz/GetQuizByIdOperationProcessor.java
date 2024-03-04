package sit.tuvarna.bg.core.processor.quiz;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.enums.QuestionType;
import sit.tuvarna.bg.api.exception.QuizNotFoundException;
import sit.tuvarna.bg.api.model.AnswerModel;
import sit.tuvarna.bg.api.model.QuestionModel;
import sit.tuvarna.bg.api.operations.quiz.getbyid.GetQuizByIdOperation;
import sit.tuvarna.bg.api.operations.quiz.getbyid.GetQuizByIdRequest;
import sit.tuvarna.bg.api.operations.quiz.getbyid.GetQuizByIdResponse;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.repository.QuizRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetQuizByIdOperationProcessor implements GetQuizByIdOperation {
    private final QuizRepository quizRepository;

    @Override
    @Transactional
    public GetQuizByIdResponse process(GetQuizByIdRequest request) {
        Quiz quiz = quizRepository.findById(UUID.fromString(request.getQuizId()))
                .orElseThrow(QuizNotFoundException::new);

        List<QuestionModel> questionModels = quiz.getQuestions().stream().map(question ->
                QuestionModel.builder()
                        .question(question.getQuestion())
                        .image(question.getImage())
                        .questionType(QuestionType.valueOf(question.getType().name()))
                        .answers(question.getAnswers().stream().map(answer ->
                                AnswerModel.builder()
                                        .content(answer.getContent())
                                        .isCorrect(answer.getIsCorrect())
                                        .build()
                        ).toList())
                        .build()
        ).toList();

        return GetQuizByIdResponse.builder()
                .title(quiz.getTitle())
                .category(quiz.getCategory().getCategory())
                .userEmail(quiz.getCreatorEmail())
                .questions(questionModels)
                .build();
    }
}
