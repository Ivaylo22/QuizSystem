package sit.tuvarna.bg.core.processor.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.tuvarna.bg.api.enums.QuestionType;
import sit.tuvarna.bg.api.exception.TestNotFoundException;
import sit.tuvarna.bg.api.model.AnswerModel;
import sit.tuvarna.bg.api.model.QuestionModel;
import sit.tuvarna.bg.api.model.TestSummaryModel;
import sit.tuvarna.bg.api.operations.test.gettestsummary.GetTestSummaryOperation;
import sit.tuvarna.bg.api.operations.test.gettestsummary.GetTestSummaryRequest;
import sit.tuvarna.bg.api.operations.test.gettestsummary.GetTestSummaryResponse;
import sit.tuvarna.bg.persistence.entity.Answer;
import sit.tuvarna.bg.persistence.entity.Question;
import sit.tuvarna.bg.persistence.entity.QuestionAttempt;
import sit.tuvarna.bg.persistence.entity.Test;
import sit.tuvarna.bg.persistence.repository.QuestionAttemptRepository;
import sit.tuvarna.bg.persistence.repository.QuestionRepository;
import sit.tuvarna.bg.persistence.repository.TestRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetTestSummaryOperationProcessor implements GetTestSummaryOperation {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final QuestionAttemptRepository questionAttemptRepository;

    @Override
    @Transactional(readOnly = true)
    public GetTestSummaryResponse process(GetTestSummaryRequest request) {
        UUID testId = UUID.fromString(request.getTestId());

        Test test = testRepository.findById(testId)
                .orElseThrow(TestNotFoundException::new);
        List<Question> questions = questionRepository.findAllByTestId(testId);
        List<UUID> questionIds = questions.stream()
                .map(Question::getId)
                .toList();

        // Fetch all question attempts for the given questions
        List<QuestionAttempt> allAttempts = questionAttemptRepository.findAllByQuestionIdIn(questionIds);

        // Calculate the attempt counts for each question
        Map<UUID, Long> questionAttemptCounts = allAttempts.stream()
                .collect(Collectors.groupingBy(attempt -> attempt.getQuestion().getId(), Collectors.counting()));

        // Calculate the count of each answer
        Map<UUID, Map<String, Long>> answerStatistics = new HashMap<>();
        for (QuestionAttempt attempt : allAttempts) {
            UUID questionId = attempt.getQuestion().getId();
            for (String answerContent : attempt.getChosenAnswers()) {
                answerStatistics
                        .computeIfAbsent(questionId, k -> new HashMap<>())
                        .merge(answerContent, 1L, Long::sum);
            }
        }

        // Calculate percentages and prepare answer models
        List<QuestionModel> questionModels = questions.stream().map(question -> {
            Long totalAttempts = questionAttemptCounts.getOrDefault(question.getId(), 1L); // Use 1L to avoid division by zero
            List<AnswerModel> answerModels;

            if (question.getType() == sit.tuvarna.bg.persistence.enums.QuestionType.OPEN) {
                // For open questions, show top 4 answers and determine correctness
                Set<String> correctAnswers = question.getAnswers().stream()
                        .filter(Answer::getIsCorrect)
                        .map(Answer::getContent)
                        .collect(Collectors.toSet());

                Map<String, Long> openAnswerCounts = answerStatistics.getOrDefault(question.getId(), Collections.emptyMap());
                answerModels = openAnswerCounts.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(4)
                        .map(entry -> AnswerModel.builder()
                                .content(entry.getKey())
                                .isCorrect(correctAnswers.contains(entry.getKey()))
                                .percentage((entry.getValue() * 100.0) / totalAttempts)
                                .build())
                        .collect(Collectors.toList());
            } else {
                answerModels = question.getAnswers().stream()
                        .map(answer -> {
                            Long count = answerStatistics.getOrDefault(question.getId(), Collections.emptyMap()).getOrDefault(answer.getContent(), 0L);
                            double percentage = (count * 100.0) / totalAttempts;
                            return AnswerModel.builder()
                                    .id(answer.getId().toString())
                                    .content(answer.getContent())
                                    .isCorrect(answer.getIsCorrect())
                                    .percentage(percentage)
                                    .build();
                        })
                        .collect(Collectors.toList());
            }

            return QuestionModel.builder()
                    .id(question.getId().toString())
                    .question(question.getQuestion())
                    .questionType(QuestionType.valueOf(question.getType().name()))
                    .answers(answerModels)
                    .build();
        }).collect(Collectors.toList());

        return GetTestSummaryResponse.builder()
                .model(TestSummaryModel.builder()
                        .testId(test.getId().toString())
                        .title(test.getTitle())
                        .questions(questionModels)
                        .build())
                .build();
    }
}
