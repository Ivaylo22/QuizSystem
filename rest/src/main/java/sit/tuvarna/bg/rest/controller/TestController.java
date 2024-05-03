package sit.tuvarna.bg.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.tuvarna.bg.api.operations.test.create.CreateTestOperation;
import sit.tuvarna.bg.api.operations.test.create.CreateTestRequest;
import sit.tuvarna.bg.api.operations.test.create.CreateTestResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/test")
public class TestController {

    private final CreateTestOperation createTest;

    @PostMapping("/create")
    public ResponseEntity<CreateTestResponse> createTest(@RequestBody @Valid CreateTestRequest request) {
        return new ResponseEntity<>(createTest.process(request), HttpStatus.CREATED);
    }
}
