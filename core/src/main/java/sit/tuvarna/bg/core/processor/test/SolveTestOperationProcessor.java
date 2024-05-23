package sit.tuvarna.bg.core.processor.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.QuestionNotFoundException;
import sit.tuvarna.bg.api.exception.TestNotFoundException;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.api.model.QuestionAttemptModel;
import sit.tuvarna.bg.api.operations.test.solve.SolveTestOperation;
import sit.tuvarna.bg.api.operations.test.solve.SolveTestRequest;
import sit.tuvarna.bg.api.operations.test.solve.SolveTestResponse;
import sit.tuvarna.bg.persistence.entity.*;
import sit.tuvarna.bg.persistence.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SolveTestOperationProcessor implements SolveTestOperation {

    private final UsersTestsRepository usersTestsRepository;
    private final QuestionAttemptRepository questionAttemptRepository;
    private final UserRepository userRepository;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;

    @Override
    public SolveTestResponse process(SolveTestRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);
        Test test = testRepository.findById(UUID.fromString(request.getTestId()))
                .orElseThrow(TestNotFoundException::new);

        double totalPoints = calculateTotalPoints(request);
        double finalScore = calculateGrade(test.getScoringFormula(), request.getTotalPoints(), totalPoints);

        UsersTests usersTests = UsersTests.builder()
                .user(user)
                .test(test)
                .attemptTime(LocalDateTime.now())
                .totalPoints(request.getTotalPoints())
                .finalScore(finalScore)
                .build();

        usersTests = usersTestsRepository.save(usersTests);

        saveQuestionAttempts(request, usersTests);

        return SolveTestResponse.builder()
                .grade(finalScore)
                .build();
    }

    private double calculateTotalPoints(SolveTestRequest request) {
        return request.getQuestionAttempts().stream()
                .mapToDouble(attemptDto -> {
                    Question question = questionRepository.findById(UUID.fromString(attemptDto.getQuestionId()))
                            .orElseThrow(QuestionNotFoundException::new);
                    return question.getMaximumPoints();
                })
                .sum();
    }

    private void saveQuestionAttempts(SolveTestRequest request, UsersTests usersTests) {
        for (QuestionAttemptModel attemptDto : request.getQuestionAttempts()) {
            Question question = questionRepository.findById(UUID.fromString(attemptDto.getQuestionId()))
                    .orElseThrow(QuestionNotFoundException::new);

            QuestionAttempt questionAttempt = QuestionAttempt.builder()
                    .usersTests(usersTests)
                    .question(question)
                    .pointsAwarded(attemptDto.getPointsAwarded())
                    .build();

            questionAttemptRepository.save(questionAttempt);
        }
    }

    private Double calculateGrade(String formula, Double points, Double totalPoints) {
        if (totalPoints == 0) {
            throw new IllegalArgumentException("Total points cannot be zero.");
        }

        Double grade;
        switch (formula) {
            case "formula1":
                grade = 2 + (points / totalPoints) * 4;
                break;
            case "formula2":
                grade = (points / totalPoints) * 6;
                break;
            case "formula3":
                grade = 1 + (points / totalPoints) * 5;
                break;
            default:
                throw new IllegalArgumentException("Invalid formula.");
        }

        return round(grade, 2);
    }

    private Double round(Double value, int places) {
        if (places < 0) throw new IllegalArgumentException("Decimal places must be non-negative.");

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
