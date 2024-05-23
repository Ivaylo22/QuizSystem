package sit.tuvarna.bg.core.processor.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.TestNotFoundException;
import sit.tuvarna.bg.api.operations.test.generatekey.GenerateKeyOperation;
import sit.tuvarna.bg.api.operations.test.generatekey.GenerateKeyRequest;
import sit.tuvarna.bg.api.operations.test.generatekey.GenerateKeyResponse;
import sit.tuvarna.bg.persistence.entity.Test;
import sit.tuvarna.bg.persistence.repository.TestRepository;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenerateKeyOperationProcessor implements GenerateKeyOperation {

    private final TestRepository testRepository;

    @Override
    public GenerateKeyResponse process(GenerateKeyRequest request) {
        Test test = testRepository.findById(UUID.fromString(request.getTestId()))
                .orElseThrow(TestNotFoundException::new);
        test.setAccessKey(generateKey());
        testRepository.save(test);
        return GenerateKeyResponse.builder()
                .accessKey(test.getAccessKey())
                .build();
    }

    private String generateKey() {
        SecureRandom random = new SecureRandom();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            key.append(random.nextInt(10));
        }

        return key.toString();
    }
}
