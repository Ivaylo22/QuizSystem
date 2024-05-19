package sit.tuvarna.bg.core.processor.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import sit.tuvarna.bg.persistence.repository.QuestionRepository;
import sit.tuvarna.bg.persistence.repository.SectionRepository;
import sit.tuvarna.bg.persistence.repository.SubjectRepository;
import sit.tuvarna.bg.persistence.repository.TestRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateTestOperationProcessor implements CreateTestOperation {

    private final TestRepository testRepository;
    private final SubjectRepository subjectRepository;
    private final SectionRepository sectionRepository;
    private final QuestionRepository questionRepository;

    @Override
    @Transactional
    public CreateTestResponse process(CreateTestRequest request) {
        Subject subject = subjectRepository.findBySubject(request.getSubject())
                .orElseGet(() -> {
                    Subject newSubject = Subject.builder()
                            .subject(request.getSubject())
                            .build();
                    return subjectRepository.save(newSubject);
                });

        Test test = Test.builder()
                .title(request.getTitle())
                .grade(request.getGrade())
                .subject(subject)
                .status(TestStatus.valueOf(request.getStatus().name()))
                .creatorEmail(request.getCreatorEmail())
                .mixedQuestions(request.getHasMixedQuestions())
                .minutesToSolve(request.getMinutesToSolve())
                .scoringFormula(request.getScoringFormula())
                .build();

        test = testRepository.save(test); // Save the test first to get its ID

        List<Section> sections = new ArrayList<>();
        Map<String, UUID> questionIdMap = new HashMap<>();

        for (SectionModel sectionModel : request.getSections()) {
            Section section = Section.builder()
                    .totalQuestionsCount(sectionModel.getTotalQuestionsCount())
                    .usedQuestionsCount(sectionModel.getUsedQuestionsCount())
                    .test(test)
                    .build();

            section = sectionRepository.save(section); // Save the section to get its ID

            List<Question> questions = new ArrayList<>();
            for (QuestionModel questionModel : sectionModel.getQuestions()) {
                Question question = Question.builder()
                        .question(questionModel.getQuestion())
                        .image(questionModel.getImage())
                        .type(QuestionType.valueOf(questionModel.getQuestionType().name()))
                        .section(section)
                        .maximumPoints(questionModel.getMaximumPoints())
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
                question = questionRepository.save(question); // Save the question to get its ID
                questionIdMap.put(questionModel.getId(), question.getId()); // Map original question ID to the newly generated one
                questions.add(question);
            }
            section.setQuestions(questions);
            sections.add(section);
        }

        test.setSections(sections);

        try {
            test = testRepository.save(test); // Save the test with sections and questions

            return CreateTestResponse.builder()
                    .id(test.getId().toString())
                    .questionIdMap(questionIdMap)
                    .build();
        } catch (Exception e) {
            throw new DatabaseException();
        }
    }
}
