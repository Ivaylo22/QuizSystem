package sit.tuvarna.bg.core.processor.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.base.ProcessorRequest;
import sit.tuvarna.bg.api.base.ProcessorResponse;
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
import sit.tuvarna.bg.persistence.repository.TestRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetTestByIdOperationProcessor implements GetTestByIdOperation {

    private final TestRepository testRepository;

    @Override
    public GetTestByIdResponse process(GetTestByIdRequest request) {
        Test test = testRepository.findById(UUID.fromString(request.getTestId()))
                .orElseThrow(TestNotFoundException::new);
        return toResponse(test);
    }

    public GetTestByIdResponse toResponse(Test test) {
        return GetTestByIdResponse.builder()
                .id(test.getId().toString())
                .title(test.getTitle())
                .grade(test.getGrade())
                .subject(test.getSubject().getSubject())
                .creatorEmail(test.getCreatorEmail())
                .minutesToSolve(test.getMinutesToSolve())
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
