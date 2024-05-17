package sit.tuvarna.bg.core.processor.test;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.model.TestModel;
import sit.tuvarna.bg.api.operations.test.getpublictests.GetPublicTestsOperation;
import sit.tuvarna.bg.api.operations.test.getpublictests.GetPublicTestsRequest;
import sit.tuvarna.bg.api.operations.test.getpublictests.GetPublicTestsResponse;
import sit.tuvarna.bg.persistence.entity.Test;
import sit.tuvarna.bg.persistence.enums.TestStatus;
import sit.tuvarna.bg.persistence.repository.TestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPublicTestsOperationProcessor implements GetPublicTestsOperation {

    private final TestRepository testRepository;
    private final ConversionService conversionService;

    @Override
    public GetPublicTestsResponse process(GetPublicTestsRequest request) {
        List<Test> publicTests = testRepository.getAllByStatus(TestStatus.PUBLIC);
        return GetPublicTestsResponse.builder()
                .tests(publicTests.stream()
                        .map(test -> conversionService.convert(test, TestModel.class))
                        .toList())
                .build();
    }
}
