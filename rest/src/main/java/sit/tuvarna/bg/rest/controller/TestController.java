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
import sit.tuvarna.bg.api.operations.test.deletekey.DeleteKeyOperation;
import sit.tuvarna.bg.api.operations.test.deletekey.DeleteKeyRequest;
import sit.tuvarna.bg.api.operations.test.deletekey.DeleteKeyResponse;
import sit.tuvarna.bg.api.operations.test.generatekey.GenerateKeyOperation;
import sit.tuvarna.bg.api.operations.test.generatekey.GenerateKeyRequest;
import sit.tuvarna.bg.api.operations.test.generatekey.GenerateKeyResponse;
import sit.tuvarna.bg.api.operations.test.getattemptdata.GetAttemptDataOperation;
import sit.tuvarna.bg.api.operations.test.getattemptdata.GetAttemptDataRequest;
import sit.tuvarna.bg.api.operations.test.getattemptdata.GetAttemptDataResponse;
import sit.tuvarna.bg.api.operations.test.getbyaccesskey.GetByAccessKeyOperation;
import sit.tuvarna.bg.api.operations.test.getbyaccesskey.GetByAccessKeyRequest;
import sit.tuvarna.bg.api.operations.test.getbyaccesskey.GetByAccessKeyResponse;
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
import sit.tuvarna.bg.api.operations.test.gettestattempts.GetTestAttemptsOperation;
import sit.tuvarna.bg.api.operations.test.gettestattempts.GetTestAttemptsRequest;
import sit.tuvarna.bg.api.operations.test.gettestattempts.GetTestAttemptsResponse;
import sit.tuvarna.bg.api.operations.test.gettestsummary.GetTestSummaryOperation;
import sit.tuvarna.bg.api.operations.test.gettestsummary.GetTestSummaryRequest;
import sit.tuvarna.bg.api.operations.test.gettestsummary.GetTestSummaryResponse;
import sit.tuvarna.bg.api.operations.test.solve.SolveTestOperation;
import sit.tuvarna.bg.api.operations.test.solve.SolveTestRequest;
import sit.tuvarna.bg.api.operations.test.solve.SolveTestResponse;
import sit.tuvarna.bg.api.operations.test.updateattemptpoints.UpdateAttemptPointsOperation;
import sit.tuvarna.bg.api.operations.test.updateattemptpoints.UpdateAttemptPointsRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/test")
public class TestController {

    private final CreateTestOperation createTest;
    private final GetSubjectsOperation getSubjects;
    private final GetPublicTestsOperation getPublicTests;
    private final GetTestByIdOperation getTest;
    private final GetByAccessKeyOperation getByAccessKey;
    private final SolveTestOperation solveTest;
    private final GetMyTestsOperation getMyTests;
    private final GenerateKeyOperation generateKey;
    private final DeleteKeyOperation deleteKey;
    private final GetTestSummaryOperation getTestSummary;
    private final GetTestAttemptsOperation getTestAttempts;
    private final GetAttemptDataOperation getAttemptData;
    private final UpdateAttemptPointsOperation updateAttemptPoints;

    @GetMapping("/get-attempt-data")
    public ResponseEntity<GetAttemptDataResponse> getAttemptData(@RequestParam String testId, @RequestParam String userEmail) {
        GetAttemptDataRequest request = GetAttemptDataRequest.builder()
                .testId(testId)
                .userEmail(userEmail)
                .build();
        return ResponseEntity.ok(getAttemptData.process(request));
    }

    @GetMapping("/get-test-summary")
    public ResponseEntity<GetTestSummaryResponse> getTestSummary(@RequestParam @NotBlank(message = "TestId is required") String testId) {
        GetTestSummaryRequest request = GetTestSummaryRequest.builder()
                .testId(testId)
                .build();
        return new ResponseEntity<>(getTestSummary.process(request), HttpStatus.OK);
    }

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

    @GetMapping("/get-by-access-key")
    public ResponseEntity<GetByAccessKeyResponse> getTestByAccessKey(@RequestParam @NotBlank(message = "AccessKey is required") String accessKey,
                                                                     @RequestParam @NotBlank(message = "User email is required") String userEmail) {
        GetByAccessKeyRequest request = GetByAccessKeyRequest.builder()
                .accessKey(accessKey)
                .userEmail(userEmail)
                .build();
        return new ResponseEntity<>(getByAccessKey.process(request), HttpStatus.OK);
    }

    @GetMapping("/get-test-attempts")
    public ResponseEntity<GetTestAttemptsResponse> getTestAttempts(@RequestParam @NotBlank(message = "Test id is required") String testId) {
        GetTestAttemptsRequest request = GetTestAttemptsRequest.builder().testId(testId).build();
        return new ResponseEntity<>(getTestAttempts.process(request), HttpStatus.OK);
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

    @PostMapping("/update-attempt-points")
    public ResponseEntity<Void> updateAttemptPoints(@RequestBody UpdateAttemptPointsRequest request) {
        updateAttemptPoints.process(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity<CreateTestResponse> createTest(@RequestBody @Valid CreateTestRequest request) {
        return new ResponseEntity<>(createTest.process(request), HttpStatus.CREATED);
    }

    @PostMapping("/solve")
    public ResponseEntity<SolveTestResponse> solveTest(@RequestBody @Valid SolveTestRequest request) {
        return new ResponseEntity<>(solveTest.process(request), HttpStatus.OK);
    }

    @PostMapping("/generate-key")
    public ResponseEntity<GenerateKeyResponse> generateKey(@RequestBody @Valid GenerateKeyRequest request) {
        return new ResponseEntity<>(generateKey.process(request), HttpStatus.OK);
    }

    @PostMapping("/delete-key")
    public ResponseEntity<DeleteKeyResponse> deleteKey(@RequestBody @Valid DeleteKeyRequest request) {
        return new ResponseEntity<>(deleteKey.process(request), HttpStatus.OK);
    }
}
