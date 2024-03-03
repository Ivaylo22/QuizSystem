package sit.tuvarna.bg.rest.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.tuvarna.bg.api.operations.quiz.create.CreateQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.create.CreateQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.create.CreateQuizResponse;
import sit.tuvarna.bg.api.operations.quiz.getallforuser.GetAllQuizzesForUserOperation;
import sit.tuvarna.bg.api.operations.quiz.getallforuser.GetAllQuizzesForUserRequest;
import sit.tuvarna.bg.api.operations.quiz.getallforuser.GetAllQuizzesForUserResponse;
import sit.tuvarna.bg.api.operations.quiz.getcategories.GetCategoriesOperation;
import sit.tuvarna.bg.api.operations.quiz.getcategories.GetCategoriesRequest;
import sit.tuvarna.bg.api.operations.quiz.getcategories.GetCategoriesResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/quiz")
public class QuizController {

    private final GetCategoriesOperation getCategories;
    private final CreateQuizOperation createQuiz;
    private final GetAllQuizzesForUserOperation getAllForUser;

    @GetMapping("/categories")
    public ResponseEntity<GetCategoriesResponse> getCategories() {
        GetCategoriesRequest request = GetCategoriesRequest.builder().build();
        return new ResponseEntity<>(getCategories.process(request), HttpStatus.OK);
    }

    @GetMapping("/get-all-for-user")
    public ResponseEntity<GetAllQuizzesForUserResponse> getAllForUser(
            @RequestParam @NotBlank(message = "User email is required") String email
    ) {
        GetAllQuizzesForUserRequest request = GetAllQuizzesForUserRequest.builder()
                .userEmail(email).build();
        return new ResponseEntity<>(getAllForUser.process(request), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<CreateQuizResponse> createQuiz(
            @RequestBody @Valid CreateQuizRequest request
    ) {
        return new ResponseEntity<>(createQuiz.process(request), HttpStatus.OK);
    }
}
