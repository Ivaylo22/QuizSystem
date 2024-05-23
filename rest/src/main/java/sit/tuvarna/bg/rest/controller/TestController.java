package sit.tuvarna.bg.rest.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
import sit.tuvarna.bg.api.operations.test.getmytests.GetMyTestsOperation;
import sit.tuvarna.bg.api.operations.test.getmytests.GetMyTestsRequest;
import sit.tuvarna.bg.api.operations.test.getmytests.GetMyTestsResponse;
import sit.tuvarna.bg.api.operations.test.getpublictests.GetPublicTestsOperation;
import sit.tuvarna.bg.api.operations.test.getpublictests.GetPublicTestsRequest;
import sit.tuvarna.bg.api.operations.test.getpublictests.GetPublicTestsResponse;
import sit.tuvarna.bg.api.operations.test.getsubjects.GetSubjectsOperation;
import sit.tuvarna.bg.api.operations.test.getsubjects.GetSubjectsRequest;
import sit.tuvarna.bg.api.operations.test.getsubjects.GetSubjectsResponse;
import sit.tuvarna.bg.api.operations.test.solve.SolveTestOperation;
import sit.tuvarna.bg.api.operations.test.solve.SolveTestRequest;
import sit.tuvarna.bg.api.operations.test.solve.SolveTestResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/test")
public class TestController {

    private final CreateTestOperation createTest;
    private final GetSubjectsOperation getSubjects;
    private final GetPublicTestsOperation getPublicTests;
    private final GetTestByIdOperation getTest;
    private final SolveTestOperation solveTest;
    private final GetMyTestsOperation getMyTests;

    @GetMapping("/get-mine")
    public ResponseEntity<GetMyTestsResponse> getMyTests(@RequestParam @NotBlank(message = "Email is required") String userEmail) {
        GetMyTestsRequest request = GetMyTestsRequest.builder()
                .email(userEmail)
                .build();
        return new ResponseEntity<>(getMyTests.process(request), HttpStatus.OK);
    }

    @GetMapping("/get-by-id")
    public ResponseEntity<GetTestByIdResponse> getTestById(@RequestParam @NotBlank(message = "TestId is required") String testId) {
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

    @PostMapping("/solve")
    public ResponseEntity<SolveTestResponse> solveTest(@RequestBody @Valid SolveTestRequest request) {
        return new ResponseEntity<>(solveTest.process(request), HttpStatus.OK);
    }
}
