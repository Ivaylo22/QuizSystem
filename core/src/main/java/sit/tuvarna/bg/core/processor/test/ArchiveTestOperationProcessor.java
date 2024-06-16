package sit.tuvarna.bg.core.processor.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.TestNotFoundException;
import sit.tuvarna.bg.api.operations.test.archive.ArchiveTestOperation;
import sit.tuvarna.bg.api.operations.test.archive.ArchiveTestRequest;
import sit.tuvarna.bg.api.operations.test.archive.ArchiveTestResponse;
import sit.tuvarna.bg.persistence.entity.Test;
import sit.tuvarna.bg.persistence.enums.TestStatus;
import sit.tuvarna.bg.persistence.repository.TestRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArchiveTestOperationProcessor implements ArchiveTestOperation {

    private final TestRepository testRepository;

    @Override
    public ArchiveTestResponse process(ArchiveTestRequest request) {
        Test test = testRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(TestNotFoundException::new);

        test.setStatus(TestStatus.ARCHIVED);
        testRepository.save(test);

        return ArchiveTestResponse.builder()
                .status(test.getStatus().name())
                .build();
    }
}
