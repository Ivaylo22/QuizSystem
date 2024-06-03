package sit.tuvarna.bg.core.processor.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.tuvarna.bg.api.enums.QuestionType;
import sit.tuvarna.bg.api.exception.TestNotFoundException;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.api.exception.UserTestNotFoundException;
import sit.tuvarna.bg.api.model.AnswerModel;
import sit.tuvarna.bg.api.model.QuestionModel;
import sit.tuvarna.bg.api.model.TestAttemptModel;
import sit.tuvarna.bg.api.operations.test.getattemptdata.GetAttemptDataOperation;
import sit.tuvarna.bg.api.operations.test.getattemptdata.GetAttemptDataRequest;
import sit.tuvarna.bg.api.operations.test.getattemptdata.GetAttemptDataResponse;
import sit.tuvarna.bg.persistence.entity.*;
import sit.tuvarna.bg.persistence.repository.TestRepository;
import sit.tuvarna.bg.persistence.repository.UserRepository;
import sit.tuvarna.bg.persistence.repository.UsersTestsRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetAttemptDataOperationProcessor implements GetAttemptDataOperation {

    private final UsersTestsRepository usersTestsRepository;
    private final UserRepository userRepository;
    private final TestRepository testRepository;

    @Override
    @Transactional
    public GetAttemptDataResponse process(GetAttemptDataRequest request) {
        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(UserNotFoundException::new);
        Test test = testRepository.findById(UUID.fromString(request.getTestId()))
                .orElseThrow(TestNotFoundException::new);
        UsersTests usersTests = usersTestsRepository.findByTestAndUser(test, user)
                .orElseThrow(UserTestNotFoundException::new);

        List<QuestionAttempt> questionAttempts = usersTests.getQuestionAttempts();
        List<QuestionModel> shownQuestions = questionAttempts.stream()
                .map(this::toQuestionModel)
                .toList();

        return GetAttemptDataResponse.builder()
                .testAttempt(toUserAttemptModel(usersTests))
                .questions(shownQuestions)
                .build();
    }

    private QuestionModel toQuestionModel(QuestionAttempt attempt) {
        Question question = attempt.getQuestion();
        List<String> chosenAnswers = attempt.getChosenAnswers();

        return QuestionModel.builder()
                .id(question.getId().toString())
                .question(question.getQuestion())
                .image(question.getImage())
                .maximumPoints(question.getMaximumPoints())
                .earnedPoints(attempt.getPointsAwarded().intValue())
                .questionType(QuestionType.valueOf(question.getType().name()))
                .chosenAnswers(chosenAnswers)
                .answers(question.getAnswers().stream()
                        .map(this::toAnswerModel)
                        .toList())
                .build();
    }

    private AnswerModel toAnswerModel(Answer answer) {
        return AnswerModel.builder()
                .id(answer.getId().toString())
                .content(answer.getContent())
                .isCorrect(answer.getIsCorrect())
                .build();
    }

    private TestAttemptModel toUserAttemptModel(UsersTests usersTests) {
        return TestAttemptModel.builder()
                .userId(usersTests.getUser().getId().toString())
                .testId(usersTests.getTest().getId().toString())
                .userEmail(usersTests.getUser().getEmail())
                .testName(usersTests.getTest().getTitle())
                .solvedAt(usersTests.getAttemptTime().toString())
                .grade(usersTests.getFinalScore().toString())
                .build();
    }
}
