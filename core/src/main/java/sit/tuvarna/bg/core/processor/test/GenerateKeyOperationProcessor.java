package sit.tuvarna.bg.core.processor.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.enums.NotificationType;
import sit.tuvarna.bg.api.exception.TestNotFoundException;
import sit.tuvarna.bg.api.operations.test.generatekey.GenerateKeyOperation;
import sit.tuvarna.bg.api.operations.test.generatekey.GenerateKeyRequest;
import sit.tuvarna.bg.api.operations.test.generatekey.GenerateKeyResponse;
import sit.tuvarna.bg.core.externalservices.NotificationService;
import sit.tuvarna.bg.persistence.entity.Test;
import sit.tuvarna.bg.persistence.repository.TestRepository;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenerateKeyOperationProcessor implements GenerateKeyOperation {

    private final TestRepository testRepository;
    private final NotificationService notificationService;

    @Override
    public GenerateKeyResponse process(GenerateKeyRequest request) {
        Test test = testRepository.findById(UUID.fromString(request.getTestId()))
                .orElseThrow(TestNotFoundException::new);
        String generatedKey = generateKey();
        test.setAccessKey(generatedKey);
        testRepository.save(test);

        notificationService.sendNotificationToUser(NotificationType.ACCESS_KEY, generatedKey, test.getCreatorEmail());

        return GenerateKeyResponse.builder()
                .accessKey(generatedKey)
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
