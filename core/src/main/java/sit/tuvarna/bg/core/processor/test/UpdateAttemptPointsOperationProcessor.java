package sit.tuvarna.bg.core.processor.test;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.QuestionNotFoundException;
import sit.tuvarna.bg.api.exception.TestNotFoundException;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.api.exception.UserTestNotFoundException;
import sit.tuvarna.bg.api.operations.test.updateattemptpoints.UpdateAttemptPointsOperation;
import sit.tuvarna.bg.api.operations.test.updateattemptpoints.UpdateAttemptPointsRequest;
import sit.tuvarna.bg.api.operations.test.updateattemptpoints.UpdateAttemptPointsResponse;
import sit.tuvarna.bg.persistence.entity.*;
import sit.tuvarna.bg.persistence.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateAttemptPointsOperationProcessor implements UpdateAttemptPointsOperation {

    private final UsersTestsRepository usersTestsRepository;
    private final QuestionRepository questionRepository;
    private final QuestionAttemptRepository questionAttemptRepository;
    private final TestRepository testRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UpdateAttemptPointsResponse process(UpdateAttemptPointsRequest request) {
        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(UserNotFoundException::new);
        Test test = testRepository.findById(UUID.fromString(request.getTestId()))
                .orElseThrow(TestNotFoundException::new);
        UsersTests usersTests = usersTestsRepository.findByTestAndUser(test, user)
                .orElseThrow(UserTestNotFoundException::new);

        Double earnedPoints = updateQuestionAttempts(request, usersTests);
        double totalPoints = calculateTotalPoints(usersTests);
        double finalScore = calculateGrade(usersTests.getTest().getScoringFormula(), earnedPoints, totalPoints);

        usersTests.setTotalPoints(earnedPoints);
        usersTests.setFinalScore(finalScore);
        usersTestsRepository.save(usersTests);
        return UpdateAttemptPointsResponse.builder().build();
    }

    private Double updateQuestionAttempts(UpdateAttemptPointsRequest request, UsersTests usersTests) {
        return request.getPoints().entrySet().stream().mapToDouble(entry -> {
            UUID questionId = UUID.fromString(entry.getKey());
            Double points = entry.getValue();

            Question question = questionRepository.findById(questionId)
                    .orElseThrow(QuestionNotFoundException::new);
            QuestionAttempt questionAttempt = questionAttemptRepository.findByUsersTestsAndQuestion(usersTests, question)
                    .orElseThrow(UserTestNotFoundException::new);

            questionAttempt.setPointsAwarded(points);
            questionAttemptRepository.save(questionAttempt);

            return points;
        }).sum();
    }

    private double calculateTotalPoints(UsersTests usersTests) {
        return usersTests.getQuestionAttempts().stream()
                .mapToDouble(qa -> qa.getQuestion().getMaximumPoints())
                .sum();
    }

    private double calculateGrade(String formula, double points, double totalPoints) {
        if (totalPoints == 0) {
            throw new IllegalArgumentException("Total points cannot be zero.");
        }

        double grade = switch (formula) {
            case "formula1" -> 2 + (points / totalPoints) * 4;
            case "formula2" -> (points / totalPoints) * 6;
            case "formula3" -> 1 + (points / totalPoints) * 5;
            default -> throw new IllegalArgumentException("Invalid formula.");
        };

        return round(grade, 2);
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException("Decimal places must be non-negative.");

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}