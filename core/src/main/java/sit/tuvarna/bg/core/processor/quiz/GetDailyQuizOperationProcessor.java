package sit.tuvarna.bg.core.processor.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.QuizNotFoundException;
import sit.tuvarna.bg.api.operations.quiz.getdaily.GetDailyQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.getdaily.GetDailyQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.getdaily.GetDailyQuizResponse;
import sit.tuvarna.bg.persistence.entity.Quiz;
import sit.tuvarna.bg.persistence.repository.QuizRepository;

@Service
@RequiredArgsConstructor
public class GetDailyQuizOperationProcessor implements GetDailyQuizOperation {
    private final QuizRepository quizRepository;

    @Override
    public GetDailyQuizResponse process(GetDailyQuizRequest request) {
        Quiz quiz = quizRepository.findDailyQuiz()
                .orElseThrow(QuizNotFoundException::new);
        return GetDailyQuizResponse.builder()
                .quizId(quiz.getId().toString())
                .title(quiz.getTitle())
                .category(quiz.getCategory().getCategory())
                .averageTimeToSolve(quiz.getAverageSecondsNeeded())
                .questionsCount(quiz.getQuestions().size())
                .build();
    }
}
