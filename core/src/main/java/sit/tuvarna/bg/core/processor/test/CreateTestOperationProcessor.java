package sit.tuvarna.bg.core.processor.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.DatabaseException;
import sit.tuvarna.bg.api.model.AnswerModel;
import sit.tuvarna.bg.api.model.QuestionModel;
import sit.tuvarna.bg.api.model.SectionModel;
import sit.tuvarna.bg.api.operations.test.create.CreateTestOperation;
import sit.tuvarna.bg.api.operations.test.create.CreateTestRequest;
import sit.tuvarna.bg.api.operations.test.create.CreateTestResponse;
import sit.tuvarna.bg.persistence.entity.*;
import sit.tuvarna.bg.persistence.enums.QuestionType;
import sit.tuvarna.bg.persistence.enums.TestStatus;
import sit.tuvarna.bg.persistence.repository.SubjectRepository;
import sit.tuvarna.bg.persistence.repository.TestRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateTestOperationProcessor implements CreateTestOperation {

    private final TestRepository testRepository;
    private final SubjectRepository subjectRepository;

    @Override
    public CreateTestResponse process(CreateTestRequest request) {

        Subject subject = subjectRepository.findBySubject(request.getSubject())
                .orElseGet(() -> {
                    Subject newSubject = Subject.builder()
                            .subject(request.getSubject())
                            .build();
                    return subjectRepository.save(newSubject);
                });
        Test test = Test.builder()
                .grade(request.getGrade())
                .subject(subject)
                .status(TestStatus.PRIVATE)
                .creatorEmail(request.getCreatorEmail())
                .mixedQuestions(request.getHasMixedQuestions())
                .hasKey(request.getHasKey())
                .build();

        List<Section> sections = new ArrayList<>();
        for (SectionModel sectionModel : request.getSections()) {
            Section section = Section.builder()
                    .totalQuestionsCount(sectionModel.getTotalQuestionsCount())
                    .usedQuestionsCount(sectionModel.getUsedQuestionsCount())
                    .test(test)
                    .build();

            List<Question> questions = new ArrayList<>();
            for (QuestionModel questionModel : sectionModel.getQuestions()) {
                Question question = Question.builder()
                        .question(questionModel.getQuestion())
                        .image(questionModel.getImage())
                        .type(QuestionType.valueOf(questionModel.getQuestionType().name()))
                        .section(section)
                        .build();

                List<Answer> answers = new ArrayList<>();
                for (AnswerModel answerModel : questionModel.getAnswers()) {
                    Answer answer = Answer.builder()
                            .content(answerModel.getContent())
                            .isCorrect(answerModel.getIsCorrect())
                            .question(question)
                            .build();
                    answers.add(answer);
                }
                question.setAnswers(answers);
                questions.add(question);
            }
            section.setQuestions(questions);
            sections.add(section);
        }

        test.setSections(sections);

        try {
            Test savedTest = testRepository.save(test);
            return CreateTestResponse.builder()
                    .id(savedTest.getId().toString())
                    .build();
        } catch (Exception e) {
            throw new DatabaseException();
        }
    }
}
