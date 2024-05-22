package sit.tuvarna.bg.core.processor.test;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.enums.QuestionType;
import sit.tuvarna.bg.api.exception.TestNotFoundException;
import sit.tuvarna.bg.api.model.AnswerModel;
import sit.tuvarna.bg.api.model.QuestionModel;
import sit.tuvarna.bg.api.model.SectionModel;
import sit.tuvarna.bg.api.operations.test.getbyid.GetTestByIdOperation;
import sit.tuvarna.bg.api.operations.test.getbyid.GetTestByIdRequest;
import sit.tuvarna.bg.api.operations.test.getbyid.GetTestByIdResponse;
import sit.tuvarna.bg.persistence.entity.Answer;
import sit.tuvarna.bg.persistence.entity.Question;
import sit.tuvarna.bg.persistence.entity.Section;
import sit.tuvarna.bg.persistence.entity.Test;
import sit.tuvarna.bg.persistence.repository.AnswerRepository;
import sit.tuvarna.bg.persistence.repository.SectionRepository;
import sit.tuvarna.bg.persistence.repository.TestRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetTestByIdOperationProcessor implements GetTestByIdOperation {

    private final TestRepository testRepository;
    private final SectionRepository sectionRepository;
    private final AnswerRepository answerRepository;

    @Override
    @Transactional
    public GetTestByIdResponse process(GetTestByIdRequest request) {
        Test test = testRepository.findByIdBasic(UUID.fromString(request.getTestId()))
                .orElseThrow(TestNotFoundException::new);

        List<Section> sections = sectionRepository.findSectionsWithQuestions(UUID.fromString(request.getTestId()));

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

    private GetTestByIdResponse toResponse(Test test) {
        return GetTestByIdResponse.builder()
                .id(test.getId().toString())
                .title(test.getTitle())
                .grade(test.getGrade())
                .subject(test.getSubject().getSubject())
                .creatorEmail(test.getCreatorEmail())
                .minutesToSolve(test.getMinutesToSolve())
                .mixedQuestions(test.getMixedQuestions())
                .sections(test.getSections().stream().map(this::toSectionModel).toList())
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
