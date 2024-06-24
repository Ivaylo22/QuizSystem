package service;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sit.tuvarna.bg.api.enums.NotificationType;
import sit.tuvarna.bg.api.enums.TestStatus;
import sit.tuvarna.bg.api.exception.*;
import sit.tuvarna.bg.api.operations.test.archive.ArchiveTestRequest;
import sit.tuvarna.bg.api.operations.test.archive.ArchiveTestResponse;
import sit.tuvarna.bg.api.operations.test.create.CreateTestRequest;
import sit.tuvarna.bg.api.operations.test.create.CreateTestResponse;
import sit.tuvarna.bg.api.operations.test.generatekey.GenerateKeyRequest;
import sit.tuvarna.bg.api.operations.test.generatekey.GenerateKeyResponse;
import sit.tuvarna.bg.api.operations.test.getattemptdata.GetAttemptDataRequest;
import sit.tuvarna.bg.api.operations.test.solve.SolveTestRequest;
import sit.tuvarna.bg.api.operations.test.updateattemptpoints.UpdateAttemptPointsRequest;
import sit.tuvarna.bg.core.externalservices.NotificationService;
import sit.tuvarna.bg.core.processor.test.*;
import sit.tuvarna.bg.persistence.entity.*;
import sit.tuvarna.bg.persistence.repository.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestServiceTest {

    private static final String TEST_ID = UUID.randomUUID().toString();
    private static final String USER_EMAIL = "test@example.com";
    private static final String SUBJECT_NAME = "Math";
    @Mock
    private TestRepository testRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private SectionRepository sectionRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UsersTestsRepository usersTestsRepository;
    @Mock
    private QuestionAttemptRepository questionAttemptRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ArchiveTestOperationProcessor archiveTestOperationProcessor;
    @InjectMocks
    private CreateTestOperationProcessor createTestOperationProcessor;
    @InjectMocks
    private GenerateKeyOperationProcessor generateKeyOperationProcessor;
    @InjectMocks
    private GetAttemptDataOperationProcessor getAttemptDataOperationProcessor;
    @InjectMocks
    private SolveTestOperationProcessor solveTestOperationProcessor;
    @InjectMocks
    private UpdateAttemptPointsOperationProcessor updateAttemptPointsOperationProcessor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @org.junit.jupiter.api.Test
    public void testArchiveTest_Success() {
        Test test = Test.builder().id(UUID.fromString(TEST_ID)).status(sit.tuvarna.bg.persistence.enums.TestStatus.PUBLIC).build();
        when(testRepository.findById(UUID.fromString(TEST_ID))).thenReturn(Optional.of(test));

        ArchiveTestRequest request = ArchiveTestRequest.builder().id(TEST_ID).build();
        ArchiveTestResponse response = archiveTestOperationProcessor.process(request);

        assertNotNull(response);
        assertNotEquals(TestStatus.PUBLIC.name(), response.getStatus());
        verify(testRepository, times(1)).save(any(Test.class));
    }

    @org.junit.jupiter.api.Test
    public void testArchiveTest_NotFound() {
        when(testRepository.findById(UUID.fromString(TEST_ID))).thenReturn(Optional.empty());

        ArchiveTestRequest request = ArchiveTestRequest.builder().id(TEST_ID).build();

        assertThrows(TestNotFoundException.class, () -> archiveTestOperationProcessor.process(request));
        verify(testRepository, never()).save(any(Test.class));
    }

    @org.junit.jupiter.api.Test
    public void testCreateTest_Success() {
        CreateTestRequest request = CreateTestRequest.builder()
                .title("Math Test")
                .grade(8)
                .subject(SUBJECT_NAME)
                .status(sit.tuvarna.bg.api.enums.TestStatus.PUBLIC)
                .creatorEmail(USER_EMAIL)
                .hasMixedQuestions(false)
                .minutesToSolve(60)
                .scoringFormula("formula1")
                .sections(Collections.emptyList())
                .build();

        Subject subject = Subject.builder().subject(SUBJECT_NAME).build();
        when(subjectRepository.findBySubject(SUBJECT_NAME)).thenReturn(Optional.of(subject));
        when(testRepository.save(any(Test.class))).thenAnswer(invocation -> {
            Test test = invocation.getArgument(0);
            test.setId(UUID.fromString(TEST_ID));
            return test;
        });

        CreateTestResponse response = createTestOperationProcessor.process(request);

        assertNotNull(response);
        assertEquals(TEST_ID, response.getId());
        verify(testRepository, times(2)).save(any(Test.class));
    }

    @org.junit.jupiter.api.Test
    public void testGenerateKey_Success() {
        Test test = Test.builder().id(UUID.fromString(TEST_ID)).creatorEmail(USER_EMAIL).build();
        when(testRepository.findById(UUID.fromString(TEST_ID))).thenReturn(Optional.of(test));

        GenerateKeyRequest request = GenerateKeyRequest.builder().testId(TEST_ID).build();
        GenerateKeyResponse response = generateKeyOperationProcessor.process(request);

        assertNotNull(response);
        assertNotNull(response.getAccessKey());
        verify(testRepository, times(1)).save(any(Test.class));
        verify(notificationService, times(1)).sendNotificationToUser(any(NotificationType.class), anyString(), anyString());
    }

    @org.junit.jupiter.api.Test
    public void testGenerateKey_TestNotFound() {
        when(testRepository.findById(UUID.fromString(TEST_ID))).thenReturn(Optional.empty());

        GenerateKeyRequest request = GenerateKeyRequest.builder().testId(TEST_ID).build();

        assertThrows(TestNotFoundException.class, () -> generateKeyOperationProcessor.process(request));
        verify(testRepository, never()).save(any(Test.class));
        verify(notificationService, never()).sendNotificationToUser(any(NotificationType.class), anyString(), anyString());
    }

    @org.junit.jupiter.api.Test
    public void testGetAttemptData_UserNotFound() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        GetAttemptDataRequest request = GetAttemptDataRequest.builder()
                .userEmail(USER_EMAIL)
                .testId(TEST_ID)
                .build();

        assertThrows(UserNotFoundException.class, () -> getAttemptDataOperationProcessor.process(request));
        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
        verify(testRepository, never()).findById(any(UUID.class));
        verify(usersTestsRepository, never()).findByTestAndUser(any(Test.class), any(User.class));
    }

    @org.junit.jupiter.api.Test
    public void testGetAttemptData_TestNotFound() {
        User user = User.builder().email(USER_EMAIL).build();
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(testRepository.findById(UUID.fromString(TEST_ID))).thenReturn(Optional.empty());

        GetAttemptDataRequest request = GetAttemptDataRequest.builder()
                .userEmail(USER_EMAIL)
                .testId(TEST_ID)
                .build();

        assertThrows(TestNotFoundException.class, () -> getAttemptDataOperationProcessor.process(request));
        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
        verify(testRepository, times(1)).findById(UUID.fromString(TEST_ID));
        verify(usersTestsRepository, never()).findByTestAndUser(any(Test.class), any(User.class));
    }

    @org.junit.jupiter.api.Test
    public void testSolveTest_UserNotFound() {
        SolveTestRequest request = SolveTestRequest.builder()
                .email(USER_EMAIL)
                .testId(TEST_ID)
                .totalPoints(10.0)
                .questionAttempts(Collections.emptyList())
                .build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> solveTestOperationProcessor.process(request));
        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
        verify(testRepository, never()).findById(any(UUID.class));
        verify(questionRepository, never()).findById(any(UUID.class));
        verify(usersTestsRepository, never()).save(any(UsersTests.class));
    }

    @org.junit.jupiter.api.Test
    public void testSolveTest_TestNotFound() {
        User user = User.builder().email(USER_EMAIL).build();
        SolveTestRequest request = SolveTestRequest.builder()
                .email(USER_EMAIL)
                .testId(TEST_ID)
                .totalPoints(10.0)
                .questionAttempts(Collections.emptyList())
                .build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(testRepository.findById(UUID.fromString(TEST_ID))).thenReturn(Optional.empty());

        assertThrows(TestNotFoundException.class, () -> solveTestOperationProcessor.process(request));
        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
        verify(testRepository, times(1)).findById(UUID.fromString(TEST_ID));
        verify(questionRepository, never()).findById(any(UUID.class));
        verify(usersTestsRepository, never()).save(any(UsersTests.class));
    }


    @org.junit.jupiter.api.Test
    public void testUpdateAttemptPoints_UserNotFound() {
        Map<String, Double> pointsMap = new HashMap<>();
        pointsMap.put(UUID.randomUUID().toString(), 4.0);

        UpdateAttemptPointsRequest request = UpdateAttemptPointsRequest.builder()
                .userEmail(USER_EMAIL)
                .testId(TEST_ID)
                .points(pointsMap)
                .build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> updateAttemptPointsOperationProcessor.process(request));
        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
        verify(testRepository, never()).findById(any(UUID.class));
        verify(usersTestsRepository, never()).findByTestAndUser(any(Test.class), any(User.class));
        verify(questionRepository, never()).findById(any(UUID.class));
        verify(questionAttemptRepository, never()).findByUsersTestsAndQuestion(any(UsersTests.class), any(Question.class));
    }

    @org.junit.jupiter.api.Test
    public void testUpdateAttemptPoints_TestNotFound() {
        User user = User.builder().email(USER_EMAIL).build();
        Map<String, Double> pointsMap = new HashMap<>();
        pointsMap.put(UUID.randomUUID().toString(), 4.0);

        UpdateAttemptPointsRequest request = UpdateAttemptPointsRequest.builder()
                .userEmail(USER_EMAIL)
                .testId(TEST_ID)
                .points(pointsMap)
                .build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(testRepository.findById(UUID.fromString(TEST_ID))).thenReturn(Optional.empty());

        assertThrows(TestNotFoundException.class, () -> updateAttemptPointsOperationProcessor.process(request));
        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
        verify(testRepository, times(1)).findById(UUID.fromString(TEST_ID));
        verify(usersTestsRepository, never()).findByTestAndUser(any(Test.class), any(User.class));
        verify(questionRepository, never()).findById(any(UUID.class));
        verify(questionAttemptRepository, never()).findByUsersTestsAndQuestion(any(UsersTests.class), any(Question.class));
    }

    @org.junit.jupiter.api.Test
    public void testUpdateAttemptPoints_UserTestNotFound() {
        User user = User.builder().email(USER_EMAIL).build();
        Test test = Test.builder().id(UUID.fromString(TEST_ID)).scoringFormula("formula1").build();
        Map<String, Double> pointsMap = new HashMap<>();
        pointsMap.put(UUID.randomUUID().toString(), 4.0);

        UpdateAttemptPointsRequest request = UpdateAttemptPointsRequest.builder()
                .userEmail(USER_EMAIL)
                .testId(TEST_ID)
                .points(pointsMap)
                .build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(testRepository.findById(UUID.fromString(TEST_ID))).thenReturn(Optional.of(test));
        when(usersTestsRepository.findByTestAndUser(test, user)).thenReturn(Optional.empty());

        assertThrows(UserTestNotFoundException.class, () -> updateAttemptPointsOperationProcessor.process(request));
        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
        verify(testRepository, times(1)).findById(UUID.fromString(TEST_ID));
        verify(usersTestsRepository, times(1)).findByTestAndUser(test, user);
        verify(questionRepository, never()).findById(any(UUID.class));
        verify(questionAttemptRepository, never()).findByUsersTestsAndQuestion(any(UsersTests.class), any(Question.class));
    }
}