package sit.tuvarna.bg.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.tuvarna.bg.api.operations.test.create.CreateTestOperation;
import sit.tuvarna.bg.api.operations.test.create.CreateTestRequest;
import sit.tuvarna.bg.api.operations.test.create.CreateTestResponse;
import sit.tuvarna.bg.api.operations.test.getbyid.GetTestByIdOperation;
import sit.tuvarna.bg.api.operations.test.getbyid.GetTestByIdRequest;
import sit.tuvarna.bg.api.operations.test.getbyid.GetTestByIdResponse;
import sit.tuvarna.bg.api.operations.test.getpublictests.GetPublicTestsOperation;
import sit.tuvarna.bg.api.operations.test.getpublictests.GetPublicTestsRequest;
import sit.tuvarna.bg.api.operations.test.getpublictests.GetPublicTestsResponse;
import sit.tuvarna.bg.api.operations.test.getsubjects.GetSubjectsOperation;
import sit.tuvarna.bg.api.operations.test.getsubjects.GetSubjectsRequest;
import sit.tuvarna.bg.api.operations.test.getsubjects.GetSubjectsResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/test")
public class TestController {

    private final CreateTestOperation createTest;
    private final GetSubjectsOperation getSubjects;
    private final GetPublicTestsOperation getPublicTests;
    private final GetTestByIdOperation getTest;

    @GetMapping("/{testId}")
    public ResponseEntity<GetTestByIdResponse> getTestById(@PathVariable String testId) {
        GetTestByIdRequest request = GetTestByIdRequest.builder()
                .testId(testId)
                .build();
        return new ResponseEntity<>(getTest.process(request), HttpStatus.OK);
    }

    @GetMapping("/subjects")
    public ResponseEntity<GetSubjectsResponse> getSubjects() {
        GetSubjectsRequest request = GetSubjectsRequest.builder().build();
        return new ResponseEntity<>(getSubjects.process(request), HttpStatus.OK);
    }

    @GetMapping("/get-public-tests")
    public ResponseEntity<GetPublicTestsResponse> getPublicTests() {
        GetPublicTestsRequest request = GetPublicTestsRequest.builder().build();
        return new ResponseEntity<>(getPublicTests.process(request), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<CreateTestResponse> createTest(@RequestBody @Valid CreateTestRequest request) {
        return new ResponseEntity<>(createTest.process(request), HttpStatus.CREATED);
    }
}
