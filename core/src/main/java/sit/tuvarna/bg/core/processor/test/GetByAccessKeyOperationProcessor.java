package sit.tuvarna.bg.core.processor.test;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.enums.QuestionType;
import sit.tuvarna.bg.api.exception.TestNotFoundException;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.api.model.AnswerModel;
import sit.tuvarna.bg.api.model.QuestionModel;
import sit.tuvarna.bg.api.model.SectionModel;
import sit.tuvarna.bg.api.operations.test.getbyaccesskey.GetByAccessKeyOperation;
import sit.tuvarna.bg.api.operations.test.getbyaccesskey.GetByAccessKeyRequest;
import sit.tuvarna.bg.api.operations.test.getbyaccesskey.GetByAccessKeyResponse;
import sit.tuvarna.bg.persistence.entity.*;
import sit.tuvarna.bg.persistence.repository.*;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetByAccessKeyOperationProcessor implements GetByAccessKeyOperation {

    private final TestRepository testRepository;
    private final SectionRepository sectionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final UsersTestsRepository usersTestsRepository;

    @Override
    @Transactional
    public GetByAccessKeyResponse process(GetByAccessKeyRequest request) {
        Test test = testRepository.findByAccessKey(request.getAccessKey())
                .orElseThrow(TestNotFoundException::new);
        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(UserNotFoundException::new);

        if (usersTestsRepository.existsByUserAndTest(user, test)) {
            return GetByAccessKeyResponse.builder()
                    .alreadySolved(true)
                    .build();
        }

        List<Section> sections = sectionRepository.findSectionsWithQuestionsByAccessKey(request.getAccessKey());

        List<UUID> questionIds = sections.stream()
                .flatMap(section -> section.getQuestions().stream())
                .map(Question::getId)
                .toList();

        List<Answer> answers = answerRepository.findAnswersByQuestionIds(questionIds);

        sections.forEach(section ->
                section.getQuestions().forEach(question -> {
                    List<Answer> questionAnswers = answers.stream()
                            .filter(answer -> answer.getQuestion().getId().equals(question.getId()))
                            .toList();
                    question.getAnswers().clear();
                    question.getAnswers().addAll(questionAnswers);
                })
        );

        test.getSections().clear();
        test.getSections().addAll(sections);

        return toResponse(test);
    }

    private GetByAccessKeyResponse toResponse(Test test) {
        return GetByAccessKeyResponse.builder()
                .id(test.getId().toString())
                .title(test.getTitle())
                .grade(test.getGrade())
                .subject(test.getSubject().getSubject())
                .creatorEmail(test.getCreatorEmail())
                .minutesToSolve(test.getMinutesToSolve())
                .mixedQuestions(test.getMixedQuestions())
                .sections(test.getSections().stream().map(this::toSectionModel).toList())
                .status(test.getStatus().name())
                .alreadySolved(false)
                .build();
    }

    private SectionModel toSectionModel(Section section) {
        return SectionModel.builder()
                .id(section.getId().toString())
                .totalQuestionsCount(section.getTotalQuestionsCount())
                .usedQuestionsCount(section.getUsedQuestionsCount())
                .questions(section.getQuestions().stream().map(this::toQuestionModel).toList())
                .build();
    }

    private QuestionModel toQuestionModel(Question question) {
        return QuestionModel.builder()
                .id(question.getId().toString())
                .question(question.getQuestion())
                .image(question.getImage())
                .maximumPoints(question.getMaximumPoints())
                .questionType(QuestionType.valueOf(question.getType().name()))
                .answers(question.getAnswers().stream().map(this::toAnswerModel).toList())
                .build();
    }

    private AnswerModel toAnswerModel(Answer answer) {
        return AnswerModel.builder()
                .content(answer.getContent())
                .isCorrect(answer.getIsCorrect())
                .build();
    }
}
