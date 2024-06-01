package sit.tuvarna.bg.core.processor.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.TestNotFoundException;
import sit.tuvarna.bg.api.model.TestAttemptModel;
import sit.tuvarna.bg.api.operations.test.gettestattempts.GetTestAttemptsOperation;
import sit.tuvarna.bg.api.operations.test.gettestattempts.GetTestAttemptsRequest;
import sit.tuvarna.bg.api.operations.test.gettestattempts.GetTestAttemptsResponse;
import sit.tuvarna.bg.persistence.entity.Test;
import sit.tuvarna.bg.persistence.entity.UsersTests;
import sit.tuvarna.bg.persistence.repository.TestRepository;
import sit.tuvarna.bg.persistence.repository.UsersTestsRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetTestAttemptsOperationProcessor implements GetTestAttemptsOperation {

    private final TestRepository testRepository;
    private final UsersTestsRepository usersTestsRepository;

    @Override
    public GetTestAttemptsResponse process(GetTestAttemptsRequest request) {
        Test test = testRepository.findByIdBasic(UUID.fromString(request.getTestId()))
                .orElseThrow(TestNotFoundException::new);

        List<UsersTests> usersTests = usersTestsRepository.findAllByTest(test);
        List<TestAttemptModel> attemptModels = usersTests.stream()
                .map(usersTest -> TestAttemptModel.builder()
                        .userId(usersTest.getUser().getId().toString())
                        .testId(usersTest.getTest().getId().toString())
                        .userEmail(usersTest.getUser().getEmail())
                        .testName(usersTest.getTest().getTitle())
                        .solvedAt(usersTest.getAttemptTime().toString())
                        .grade(usersTest.getFinalScore().toString())
                        .build())
                .toList();

        return GetTestAttemptsResponse.builder()
                .attempts(attemptModels)
                .build();
    }
}
