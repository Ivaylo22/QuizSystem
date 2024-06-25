package controller;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import sit.tuvarna.bg.api.operations.quiz.active.ActiveQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.active.ActiveQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.active.ActiveQuizResponse;
import sit.tuvarna.bg.api.operations.quiz.approve.ApproveQuizOperation;
import sit.tuvarna.bg.api.operations.quiz.approve.ApproveQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.approve.ApproveQuizResponse;
import sit.tuvarna.bg.api.operations.quiz.archive.ArchiveQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.archive.ArchiveQuizResponse;
import sit.tuvarna.bg.api.operations.quiz.create.CreateQuizOperation;
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
import sit.tuvarna.bg.rest.QuizApplication;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = QuizApplication.class)
@AutoConfigureMockMvc
public class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetCategoriesOperation getCategories;

    @MockBean
    private CreateQuizOperation createQuiz;

    @MockBean
    private GetAllQuizzesForUserOperation getAllForUser;

    @MockBean
    private GetRequestedQuizzesOperation getRequestedQuizzes;

    @MockBean
    private GetQuizByIdOperation getQuizById;

    @MockBean
    private ApproveQuizOperation approveQuiz;

    @MockBean
    private DeclineQuizOperation declineQuiz;

    @MockBean
    private SolveQuizOperation solve;

    @MockBean
    private GetMyQuizzesOperation getMyQuizzes;

    @MockBean
    private ArchiveQuizOperationProcessor archiveQuiz;

    @MockBean
    private ActiveQuizOperation activeQuiz;

    @MockBean
    private GetLeaderboardOperation getLeaderboard;

    @MockBean
    private GetDailyQuizOperation getDaily;

    private static final String GET_DAILY = "/api/v1/quiz/get-daily";
    private static final String GET_LEADERBOARD = "/api/v1/quiz/get-leaderboard";
    private static final String GET_MY_QUIZZES = "/api/v1/quiz/get-mine";
    private static final String GET_CATEGORIES = "/api/v1/quiz/categories";
    private static final String GET_ALL_FOR_USER = "/api/v1/quiz/get-all-for-user";
    private static final String GET_REQUESTED = "/api/v1/quiz/get-requested";
    private static final String GET_BY_ID = "/api/v1/quiz/get-by-id";
    private static final String CREATE_QUIZ = "/api/v1/quiz/create";
    private static final String SOLVE_QUIZ = "/api/v1/quiz/solve";
    private static final String APPROVE_QUIZ = "/api/v1/quiz/approve";
    private static final String DECLINE_QUIZ = "/api/v1/quiz/decline";
    private static final String ARCHIVE_QUIZ = "/api/v1/quiz/archive";
    private static final String ACTIVE_QUIZ = "/api/v1/quiz/active";

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        Mockito.reset(getCategories, createQuiz, getAllForUser, getRequestedQuizzes, getQuizById, approveQuiz, declineQuiz, solve, getMyQuizzes, archiveQuiz, activeQuiz, getLeaderboard, getDaily);
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetDaily() throws Exception {
        GetDailyQuizResponse response = new GetDailyQuizResponse();
        GetDailyQuizRequest request = GetDailyQuizRequest.builder().build();
        when(getDaily.process(any(GetDailyQuizRequest.class))).thenReturn(response);

        mockMvc.perform(get(GET_DAILY)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetLeaderboard() throws Exception {
        GetLeaderboardResponse response = new GetLeaderboardResponse();
        GetLeaderboardRequest request = GetLeaderboardRequest.builder().build();
        when(getLeaderboard.process(any(GetLeaderboardRequest.class))).thenReturn(response);

        mockMvc.perform(get(GET_LEADERBOARD)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetMyQuizzes() throws Exception {
        GetMyQuizzesResponse response = new GetMyQuizzesResponse();
        GetMyQuizzesRequest request = GetMyQuizzesRequest.builder().email("test@example.com").build();
        when(getMyQuizzes.process(any(GetMyQuizzesRequest.class))).thenReturn(response);

        mockMvc.perform(get(GET_MY_QUIZZES)
                        .param("userEmail", "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetCategories() throws Exception {
        GetCategoriesResponse response = new GetCategoriesResponse();
        GetCategoriesRequest request = GetCategoriesRequest.builder().build();
        when(getCategories.process(any(GetCategoriesRequest.class))).thenReturn(response);

        mockMvc.perform(get(GET_CATEGORIES)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetAllForUser() throws Exception {
        GetAllQuizzesForUserResponse response = new GetAllQuizzesForUserResponse();
        GetAllQuizzesForUserRequest request = GetAllQuizzesForUserRequest.builder().userEmail("test@example.com").build();
        when(getAllForUser.process(any(GetAllQuizzesForUserRequest.class))).thenReturn(response);

        mockMvc.perform(get(GET_ALL_FOR_USER)
                        .param("email", "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetRequested() throws Exception {
        GetRequestedQuizzesResponse response = new GetRequestedQuizzesResponse();
        GetRequestedQuizzesRequest request = GetRequestedQuizzesRequest.builder().build();
        when(getRequestedQuizzes.process(any(GetRequestedQuizzesRequest.class))).thenReturn(response);

        mockMvc.perform(get(GET_REQUESTED)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetById() throws Exception {
        GetQuizByIdResponse response = new GetQuizByIdResponse();
        GetQuizByIdRequest request = GetQuizByIdRequest.builder().quizId("quizId").build();
        when(getQuizById.process(any(GetQuizByIdRequest.class))).thenReturn(response);

        mockMvc.perform(get(GET_BY_ID)
                        .param("quizId", "quizId")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testSolveQuiz() throws Exception {
        SolveQuizRequest request = SolveQuizRequest.builder()
                .quizId(UUID.randomUUID().toString())
                .email("test@example.com")
                .correctAnswers(5)
                .isDaily(false)
                .secondsToSolve(60)
                .build();
        SolveQuizResponse response = new SolveQuizResponse();
        when(solve.process(any(SolveQuizRequest.class))).thenReturn(response);

        mockMvc.perform(post(SOLVE_QUIZ)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testApproveQuiz() throws Exception {
        ApproveQuizRequest request = ApproveQuizRequest.builder()
                .id(UUID.randomUUID().toString())
                .build();
        ApproveQuizResponse response = new ApproveQuizResponse();
        when(approveQuiz.process(any(ApproveQuizRequest.class))).thenReturn(response);

        mockMvc.perform(patch(APPROVE_QUIZ)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testDeclineQuiz() throws Exception {
        DeclineQuizRequest request = DeclineQuizRequest.builder()
                .id(UUID.randomUUID().toString())
                .build();
        DeclineQuizResponse response = new DeclineQuizResponse();
        when(declineQuiz.process(any(DeclineQuizRequest.class))).thenReturn(response);

        mockMvc.perform(patch(DECLINE_QUIZ)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testArchiveQuiz() throws Exception {
        ArchiveQuizRequest request = ArchiveQuizRequest.builder()
                .id(UUID.randomUUID().toString())
                .build();
        ArchiveQuizResponse response = new ArchiveQuizResponse();
        when(archiveQuiz.process(any(ArchiveQuizRequest.class))).thenReturn(response);

        mockMvc.perform(patch(ARCHIVE_QUIZ)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testActiveQuiz() throws Exception {
        ActiveQuizRequest request = ActiveQuizRequest.builder()
                .id(UUID.randomUUID().toString())
                .build();
        ActiveQuizResponse response = new ActiveQuizResponse();
        when(activeQuiz.process(any(ActiveQuizRequest.class))).thenReturn(response);

        mockMvc.perform(patch(ACTIVE_QUIZ)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}