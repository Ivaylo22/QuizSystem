package sit.tuvarna.bg.core.processor.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.TestNotFoundException;
import sit.tuvarna.bg.api.operations.test.deletekey.DeleteKeyOperation;
import sit.tuvarna.bg.api.operations.test.deletekey.DeleteKeyRequest;
import sit.tuvarna.bg.api.operations.test.deletekey.DeleteKeyResponse;
import sit.tuvarna.bg.api.operations.test.generatekey.GenerateKeyResponse;
import sit.tuvarna.bg.persistence.entity.Test;
import sit.tuvarna.bg.persistence.repository.TestRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteKeyOperationProcessor implements DeleteKeyOperation {

    private final TestRepository testRepository;

    @Override
    public DeleteKeyResponse process(DeleteKeyRequest request) {
        Test test = testRepository.findById(UUID.fromString(request.getTestId()))
                .orElseThrow(TestNotFoundException::new);
        test.setAccessKey(null);
        testRepository.save(test);
        return DeleteKeyResponse.builder()
                .testId(test.getId().toString())
                .build();
    }
}
