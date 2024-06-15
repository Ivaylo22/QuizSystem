package sit.tuvarna.bg.core.processor.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.operations.test.getrandom.GetRandomOperation;
import sit.tuvarna.bg.api.operations.test.getrandom.GetRandomRequest;
import sit.tuvarna.bg.api.operations.test.getrandom.GetRandomResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class GetRandomOperationProcessor implements GetRandomOperation {
    @Value("${tests.directory.path}")
    private String testsDirectoryPath;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    @Override
    public GetRandomResponse process(GetRandomRequest request) {
        try {
            String filePath = String.format("%s/%s/grade%s.json", testsDirectoryPath, request.getSubject().toLowerCase(), request.getGrade());
            Resource resource = resourceLoader.getResource("classpath:" + filePath);
            byte[] jsonData = Files.readAllBytes(resource.getFile().toPath());

            List<JsonNode> tests = objectMapper.readValue(jsonData, new TypeReference<>() {
            });
            JsonNode randomTestNode = tests.get(random.nextInt(tests.size()));

            return objectMapper.treeToValue(randomTestNode, GetRandomResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Error processing the request", e);
        }
    }
}
