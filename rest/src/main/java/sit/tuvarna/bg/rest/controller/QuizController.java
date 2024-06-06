package sit.tuvarna.bg.rest.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.tuvarna.bg.api.operations.quiz.active.ActiveQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.active.ActiveQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.active.ActiveQuizResponse;
import sit.tuvarna.bg.api.operations.quiz.approve.ApproveQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.approve.ApproveQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.approve.ApproveQuizResponse;
import sit.tuvarna.bg.api.operations.quiz.archive.ArchiveQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.archive.ArchiveQuizResponse;
import sit.tuvarna.bg.api.operations.quiz.create.CreateQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.create.CreateQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.create.CreateQuizResponse;
import sit.tuvarna.bg.api.operations.quiz.decline.DeclineQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.decline.DeclineQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.decline.DeclineQuizResponse;
import sit.tuvarna.bg.api.operations.quiz.getallforuser.GetAllQuizzesForUserOperation;
import sit.tuvarna.bg.api.operations.quiz.getallforuser.GetAllQuizzesForUserRequest;
import sit.tuvarna.bg.api.operations.quiz.getallforuser.GetAllQuizzesForUserResponse;
import sit.tuvarna.bg.api.operations.quiz.getbyid.GetQuizByIdOperation;
import sit.tuvarna.bg.api.operations.quiz.getbyid.GetQuizByIdRequest;
import sit.tuvarna.bg.api.operations.quiz.getbyid.GetQuizByIdResponse;
import sit.tuvarna.bg.api.operations.quiz.getcategories.GetCategoriesOperation;
import sit.tuvarna.bg.api.operations.quiz.getcategories.GetCategoriesRequest;
import sit.tuvarna.bg.api.operations.quiz.getcategories.GetCategoriesResponse;
import sit.tuvarna.bg.api.operations.quiz.getdaily.GetDailyQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.getdaily.GetDailyQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.getdaily.GetDailyQuizResponse;
import sit.tuvarna.bg.api.operations.quiz.getmyquizzes.GetMyQuizzesOperation;
import sit.tuvarna.bg.api.operations.quiz.getmyquizzes.GetMyQuizzesRequest;
import sit.tuvarna.bg.api.operations.quiz.getmyquizzes.GetMyQuizzesResponse;
import sit.tuvarna.bg.api.operations.quiz.getrequested.GetRequestedQuizzesOperation;
import sit.tuvarna.bg.api.operations.quiz.getrequested.GetRequestedQuizzesRequest;
import sit.tuvarna.bg.api.operations.quiz.getrequested.GetRequestedQuizzesResponse;
import sit.tuvarna.bg.api.operations.quiz.leaderboard.GetLeaderboardOperation;
import sit.tuvarna.bg.api.operations.quiz.leaderboard.GetLeaderboardRequest;
import sit.tuvarna.bg.api.operations.quiz.leaderboard.GetLeaderboardResponse;
import sit.tuvarna.bg.api.operations.quiz.solve.SolveQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.solve.SolveQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.solve.SolveQuizResponse;
import sit.tuvarna.bg.core.processor.quiz.ArchiveQuizOperationProcessor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/quiz")
public class QuizController {

    private final GetCategoriesOperation getCategories;
    private final CreateQuizOperation createQuiz;
    private final GetAllQuizzesForUserOperation getAllForUser;
    private final GetRequestedQuizzesOperation getRequestedQuizzes;
    private final GetQuizByIdOperation getQuizById;
    private final ApproveQuizOperation approveQuiz;
    private final DeclineQuizOperation declineQuiz;
    private final SolveQuizOperation solve;
    private final GetMyQuizzesOperation getMyQuizzes;
    private final ArchiveQuizOperationProcessor archiveQuiz;
    private final ActiveQuizOperation activeQuiz;
    private final GetLeaderboardOperation getLeaderboard;
    private final GetDailyQuizOperation getDaily;

    @GetMapping("/get-daily")
    public ResponseEntity<GetDailyQuizResponse> getDaily() {
        GetDailyQuizRequest request = GetDailyQuizRequest.builder().build();
        return new ResponseEntity<>(getDaily.process(request), HttpStatus.OK);
    }

    @GetMapping("/get-leaderboard")
    public ResponseEntity<GetLeaderboardResponse> getLeaderBoard() {
        GetLeaderboardRequest request = GetLeaderboardRequest.builder().build();
        return new ResponseEntity<>(getLeaderboard.process(request), HttpStatus.OK);
    }

    @GetMapping("/get-mine")
    public ResponseEntity<GetMyQuizzesResponse> getMyQuizzes(@RequestParam @NotBlank(message = "Email is required") String userEmail) {
        GetMyQuizzesRequest request = GetMyQuizzesRequest.builder()
                .email(userEmail)
                .build();
        return new ResponseEntity<>(getMyQuizzes.process(request), HttpStatus.OK);
    }

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

    @GetMapping("/get-requested")
    public ResponseEntity<GetRequestedQuizzesResponse> getRequested() {
        GetRequestedQuizzesRequest request = GetRequestedQuizzesRequest.builder().build();
        return new ResponseEntity<>(getRequestedQuizzes.process(request), HttpStatus.OK);
    }

    @GetMapping("/get-by-id")
    public ResponseEntity<GetQuizByIdResponse> getById(
            @RequestParam @NotBlank(message = "QuizId is required") String quizId
    ) {
        GetQuizByIdRequest request = GetQuizByIdRequest.builder().quizId(quizId).build();
        return new ResponseEntity<>(getQuizById.process(request), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<CreateQuizResponse> createQuiz(
            @RequestBody @Valid CreateQuizRequest request
    ) {
        return new ResponseEntity<>(createQuiz.process(request), HttpStatus.OK);
    }

    @PostMapping("/solve")
    public ResponseEntity<SolveQuizResponse> solve(
            @RequestBody @Valid SolveQuizRequest request
    ) {
        return new ResponseEntity<>(solve.process(request), HttpStatus.OK);
    }

    @PatchMapping("/approve")
    public ResponseEntity<ApproveQuizResponse> approveQuiz(
            @RequestBody @Valid ApproveQuizRequest request
    ) {
        return new ResponseEntity<>(approveQuiz.process(request), HttpStatus.OK);
    }

    @PatchMapping("/decline")
    public ResponseEntity<DeclineQuizResponse> declineQuiz(
            @RequestBody @Valid DeclineQuizRequest request
    ) {
        return new ResponseEntity<>(declineQuiz.process(request), HttpStatus.OK);
    }

    @PatchMapping("/archive")
    public ResponseEntity<ArchiveQuizResponse> archiveQuiz(
            @RequestBody @Valid ArchiveQuizRequest request
    ) {
        return new ResponseEntity<>(archiveQuiz.process(request), HttpStatus.OK);
    }

    @PatchMapping("/active")
    public ResponseEntity<ActiveQuizResponse> activeQuiz(
            @RequestBody @Valid ActiveQuizRequest request
    ) {
        return new ResponseEntity<>(activeQuiz.process(request), HttpStatus.OK);
    }
}
