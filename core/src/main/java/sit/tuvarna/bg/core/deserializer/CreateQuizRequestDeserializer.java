package sit.tuvarna.bg.core.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import sit.tuvarna.bg.api.enums.QuestionType;
import sit.tuvarna.bg.api.model.AnswerModel;
import sit.tuvarna.bg.api.model.QuestionModel;
import sit.tuvarna.bg.api.operations.quiz.create.CreateQuizRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateQuizRequestDeserializer extends JsonDeserializer<CreateQuizRequest> {
    @Override
    public CreateQuizRequest deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);

        String title = node.has("title") ? node.get("title").asText() : null;
        String category = node.has("category") ? node.get("category").asText() : null;
        String userEmail = node.has("creatorEmail") ? node.get("creatorEmail").asText() : null;

        JsonNode questionsNode = node.get("questions");
        List<QuestionModel> questions = new ArrayList<>();
        if (questionsNode != null && questionsNode.isArray()) {
            for (JsonNode questionNode : questionsNode) {
                QuestionModel.QuestionModelBuilder questionBuilder = QuestionModel.builder()
                        .question(questionNode.has("question") ? questionNode.get("question").asText() : null)
                        .image(questionNode.has("image") ? questionNode.get("image").asText(null) : null)
                        .maximumPoints(questionNode.has("maximumPoints") ? questionNode.get("maximumPoints").asInt() : null)
                        .earnedPoints(questionNode.has("earnedPoints") ? questionNode.get("earnedPoints").asInt() : null);

                JsonNode questionTypeNode = questionNode.get("questionType");
                if (questionTypeNode == null) {
                    questionTypeNode = questionNode.get("type");
                }
                if (questionTypeNode != null) {
                    QuestionType questionType = QuestionType.valueOf(questionTypeNode.asText());
                    questionBuilder.questionType(questionType);
                } else {
                    throw new JsonProcessingException("Missing required field: questionType") {
                    };
                }

                JsonNode answersNode = questionNode.get("answers");
                List<AnswerModel> answers = new ArrayList<>();
                if (answersNode != null && answersNode.isArray()) {
                    for (JsonNode answerNode : answersNode) {
                        AnswerModel answer = p.getCodec().treeToValue(answerNode, AnswerModel.class);
                        answers.add(answer);
                    }
                }
                questionBuilder.answers(answers);

                questions.add(questionBuilder.build());
            }
        }

        return CreateQuizRequest.builder()
                .title(title)
                .category(category)
                .userEmail(userEmail)
                .questions(questions)
                .build();
    }
}