package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import sit.tuvarna.bg.api.operations.test.archive.ArchiveTestOperation;
import sit.tuvarna.bg.api.operations.test.archive.ArchiveTestRequest;
import sit.tuvarna.bg.api.operations.test.archive.ArchiveTestResponse;
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
import sit.tuvarna.bg.api.operations.test.getrandom.GetRandomOperation;
import sit.tuvarna.bg.api.operations.test.getrandom.GetRandomRequest;
import sit.tuvarna.bg.api.operations.test.getrandom.GetRandomResponse;
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
import sit.tuvarna.bg.rest.QuizApplication;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = QuizApplication.class)
@AutoConfigureMockMvc
public class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateTestOperation createTest;

    @MockBean
    private GetSubjectsOperation getSubjects;

    @MockBean
    private GetPublicTestsOperation getPublicTests;

    @MockBean
    private GetTestByIdOperation getTest;

    @MockBean
    private GetByAccessKeyOperation getByAccessKey;

    @MockBean
    private SolveTestOperation solveTest;

    @MockBean
    private GetMyTestsOperation getMyTests;

    @MockBean
    private GenerateKeyOperation generateKey;

    @MockBean
    private DeleteKeyOperation deleteKey;

    @MockBean
    private GetTestSummaryOperation getTestSummary;

    @MockBean
    private GetTestAttemptsOperation getTestAttempts;

    @MockBean
    private GetAttemptDataOperation getAttemptData;

    @MockBean
    private UpdateAttemptPointsOperation updateAttemptPoints;

    @MockBean
    private GetRandomOperation getRandom;

    @MockBean
    private ArchiveTestOperation archive;

    private static final String GET_RANDOM_TEST = "/api/v1/test/get-random-test";
    private static final String GET_ATTEMPT_DATA = "/api/v1/test/get-attempt-data";
    private static final String GET_TEST_SUMMARY = "/api/v1/test/get-test-summary";
    private static final String GET_MY_TESTS = "/api/v1/test/get-mine";
    private static final String GET_BY_ID = "/api/v1/test/get-by-id";
    private static final String GET_BY_ACCESS_KEY = "/api/v1/test/get-by-access-key";
    private static final String GET_TEST_ATTEMPTS = "/api/v1/test/get-test-attempts";
    private static final String GET_SUBJECTS = "/api/v1/test/subjects";
    private static final String GET_PUBLIC_TESTS = "/api/v1/test/get-public-tests";
    private static final String UPDATE_ATTEMPT_POINTS = "/api/v1/test/update-attempt-points";
    private static final String CREATE_TEST = "/api/v1/test/create";
    private static final String SOLVE_TEST = "/api/v1/test/solve";
    private static final String GENERATE_KEY = "/api/v1/test/generate-key";
    private static final String DELETE_KEY = "/api/v1/test/delete-key";
    private static final String ARCHIVE_TEST = "/api/v1/test/archive";

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        Mockito.reset(createTest, getSubjects, getPublicTests, getTest, getByAccessKey, solveTest, getMyTests,
                generateKey, deleteKey, getTestSummary, getTestAttempts, getAttemptData, updateAttemptPoints, getRandom, archive);
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetRandomTest() throws Exception {
        GetRandomResponse response = new GetRandomResponse();
        GetRandomRequest request = GetRandomRequest.builder().grade("10th").subject("Math").build();
        when(getRandom.process(any(GetRandomRequest.class))).thenReturn(response);

        mockMvc.perform(get(GET_RANDOM_TEST)
                        .param("grade", "10th")
                        .param("subject", "Math")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetAttemptData() throws Exception {
        GetAttemptDataResponse response = new GetAttemptDataResponse();
        GetAttemptDataRequest request = GetAttemptDataRequest.builder().testId("testId").userEmail("test@example.com").build();
        when(getAttemptData.process(any(GetAttemptDataRequest.class))).thenReturn(response);

        mockMvc.perform(get(GET_ATTEMPT_DATA)
                        .param("testId", "testId")
                        .param("userEmail", "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetTestSummary() throws Exception {
        GetTestSummaryResponse response = new GetTestSummaryResponse();
        GetTestSummaryRequest request = GetTestSummaryRequest.builder().testId("testId").build();
        when(getTestSummary.process(any(GetTestSummaryRequest.class))).thenReturn(response);

        mockMvc.perform(get(GET_TEST_SUMMARY)
                        .param("testId", "testId")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetMyTests() throws Exception {
        GetMyTestsResponse response = new GetMyTestsResponse();
        GetMyTestsRequest request = GetMyTestsRequest.builder().email("test@example.com").build();
        when(getMyTests.process(any(GetMyTestsRequest.class))).thenReturn(response);

        mockMvc.perform(get(GET_MY_TESTS)
                        .param("userEmail", "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetTestById() throws Exception {
        GetTestByIdResponse response = new GetTestByIdResponse();
        GetTestByIdRequest request = GetTestByIdRequest.builder().testId("testId").build();
        when(getTest.process(any(GetTestByIdRequest.class))).thenReturn(response);

        mockMvc.perform(get(GET_BY_ID)
                        .param("testId", "testId")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetByAccessKey() throws Exception {
        GetByAccessKeyResponse response = new GetByAccessKeyResponse();
        GetByAccessKeyRequest request = GetByAccessKeyRequest.builder().accessKey("accessKey").userEmail("test@example.com").build();
        when(getByAccessKey.process(any(GetByAccessKeyRequest.class))).thenReturn(response);

        mockMvc.perform(get(GET_BY_ACCESS_KEY)
                        .param("accessKey", "accessKey")
                        .param("userEmail", "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetTestAttempts() throws Exception {
        GetTestAttemptsResponse response = new GetTestAttemptsResponse();
        GetTestAttemptsRequest request = GetTestAttemptsRequest.builder().testId("testId").build();
        when(getTestAttempts.process(any(GetTestAttemptsRequest.class))).thenReturn(response);

        mockMvc.perform(get(GET_TEST_ATTEMPTS)
                        .param("testId", "testId")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetSubjects() throws Exception {
        GetSubjectsResponse response = new GetSubjectsResponse();
        GetSubjectsRequest request = GetSubjectsRequest.builder().build();
        when(getSubjects.process(any(GetSubjectsRequest.class))).thenReturn(response);

        mockMvc.perform(get(GET_SUBJECTS)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetPublicTests() throws Exception {
        GetPublicTestsResponse response = new GetPublicTestsResponse();
        GetPublicTestsRequest request = GetPublicTestsRequest.builder().build();
        when(getPublicTests.process(any(GetPublicTestsRequest.class))).thenReturn(response);

        mockMvc.perform(get(GET_PUBLIC_TESTS)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testUpdateAttemptPoints() throws Exception {
        UpdateAttemptPointsRequest request = UpdateAttemptPointsRequest.builder().build();

        mockMvc.perform(post(UPDATE_ATTEMPT_POINTS)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testCreateTest() throws Exception {
        CreateTestRequest request = CreateTestRequest.builder()
                .creatorEmail("creator@abv.bg")
                .title("Title")
                .subject("Subject")
                .build();
        CreateTestResponse response = new CreateTestResponse();
        when(createTest.process(any(CreateTestRequest.class))).thenReturn(response);

        mockMvc.perform(post(CREATE_TEST)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testSolveTest() throws Exception {
        SolveTestRequest request = SolveTestRequest.builder().build();
        SolveTestResponse response = new SolveTestResponse();
        when(solveTest.process(any(SolveTestRequest.class))).thenReturn(response);

        mockMvc.perform(post(SOLVE_TEST)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGenerateKey() throws Exception {
        GenerateKeyRequest request = GenerateKeyRequest.builder().build();
        GenerateKeyResponse response = new GenerateKeyResponse();
        when(generateKey.process(any(GenerateKeyRequest.class))).thenReturn(response);

        mockMvc.perform(post(GENERATE_KEY)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testDeleteKey() throws Exception {
        DeleteKeyRequest request = DeleteKeyRequest.builder().build();
        DeleteKeyResponse response = new DeleteKeyResponse();
        when(deleteKey.process(any(DeleteKeyRequest.class))).thenReturn(response);

        mockMvc.perform(post(DELETE_KEY)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testArchiveTest() throws Exception {
        ArchiveTestRequest request = ArchiveTestRequest.builder()
                .id(UUID.randomUUID().toString())
                .build();
        ArchiveTestResponse response = new ArchiveTestResponse();
        when(archive.process(any(ArchiveTestRequest.class))).thenReturn(response);

        mockMvc.perform(patch(ARCHIVE_TEST)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}