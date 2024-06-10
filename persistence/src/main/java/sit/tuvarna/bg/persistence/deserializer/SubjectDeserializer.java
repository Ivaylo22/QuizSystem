package sit.tuvarna.bg.persistence.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sit.tuvarna.bg.persistence.entity.Subject;
import sit.tuvarna.bg.persistence.repository.SubjectRepository;

import java.io.IOException;

@Component
public class SubjectDeserializer extends JsonDeserializer<Subject> {

    private static SubjectRepository subjectRepository;

    @Autowired
    public void setSubjectRepository(SubjectRepository repository) {
        subjectRepository = repository;
    }

    @Override
    public Subject deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        JsonNode subjectNode = node.get("subject");

        if (subjectNode == null || subjectNode.isNull()) {
            return null;
        }

        String subjectName = subjectNode.asText();
        return subjectRepository.findBySubject(subjectName)
                .orElseGet(() -> {
                    Subject newSubject = Subject.builder().subject(subjectName).build();
                    return subjectRepository.save(newSubject);
                });
    }
}