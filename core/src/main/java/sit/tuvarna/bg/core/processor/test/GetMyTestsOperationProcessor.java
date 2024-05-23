package sit.tuvarna.bg.core.processor.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.enums.TestStatus;
import sit.tuvarna.bg.api.model.TestModel;
import sit.tuvarna.bg.api.operations.test.getmytests.GetMyTestsOperation;
import sit.tuvarna.bg.api.operations.test.getmytests.GetMyTestsRequest;
import sit.tuvarna.bg.api.operations.test.getmytests.GetMyTestsResponse;
import sit.tuvarna.bg.persistence.entity.Test;
import sit.tuvarna.bg.persistence.repository.TestRepository;
import sit.tuvarna.bg.persistence.repository.UsersTestsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMyTestsOperationProcessor implements GetMyTestsOperation {

    private final TestRepository testRepository;
    private final UsersTestsRepository usersTestsRepository;

    @Override
    public GetMyTestsResponse process(GetMyTestsRequest request) {
        List<Test> tests = testRepository.getAllByCreatorEmail(request.getEmail());

        List<TestModel> testModels = tests.stream()
                .map(t -> TestModel.builder()
                        .id(t.getId().toString())
                        .title(t.getTitle())
                        .grade(t.getGrade())
                        .subject(t.getSubject().getSubject())
                        .attemptsCount(usersTestsRepository.findAllByTest(t).size())
                        .createdAt(t.getCreatedAt())
                        .status(TestStatus.valueOf(t.getStatus().name()))
                        .accessKey(t.getAccessKey())
                        .build())
                .toList();

        return GetMyTestsResponse.builder()
                .tests(testModels)
                .build();
    }
}
