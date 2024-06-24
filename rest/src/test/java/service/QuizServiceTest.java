package service;

import jdk.jfr.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.ConversionService;
import sit.tuvarna.bg.api.enums.QuestionType;
import sit.tuvarna.bg.api.exception.DatabaseException;
import sit.tuvarna.bg.api.exception.QuizAlreadyActiveException;
import sit.tuvarna.bg.api.exception.QuizNotFoundException;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.api.model.AnswerModel;
import sit.tuvarna.bg.api.model.LeaderboardModel;
import sit.tuvarna.bg.api.model.QuestionModel;
import sit.tuvarna.bg.api.model.QuizModel;
import sit.tuvarna.bg.api.operations.quiz.active.ActiveQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.active.ActiveQuizResponse;
import sit.tuvarna.bg.api.operations.quiz.create.CreateQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.create.CreateQuizResponse;
import sit.tuvarna.bg.api.operations.quiz.getallforuser.GetAllQuizzesForUserRequest;
import sit.tuvarna.bg.api.operations.quiz.getallforuser.GetAllQuizzesForUserResponse;
import sit.tuvarna.bg.api.operations.quiz.getbyid.GetQuizByIdRequest;
import sit.tuvarna.bg.api.operations.quiz.getbyid.GetQuizByIdResponse;
import sit.tuvarna.bg.api.operations.quiz.getcompleted.GetCompletedQuizzesRequest;
import sit.tuvarna.bg.api.operations.quiz.getcompleted.GetCompletedQuizzesResponse;
import sit.tuvarna.bg.api.operations.quiz.leaderboard.GetLeaderboardRequest;
import sit.tuvarna.bg.api.operations.quiz.leaderboard.GetLeaderboardResponse;
import sit.tuvarna.bg.api.operations.quiz.solve.SolveQuizRequest;
import sit.tuvarna.bg.api.operations.quiz.solve.SolveQuizResponse;
import sit.tuvarna.bg.core.processor.quiz.*;
import sit.tuvarna.bg.persistence.entity.*;
import sit.tuvarna.bg.persistence.enums.QuizStatus;
import sit.tuvarna.bg.persistence.repository.CategoryRepository;
import sit.tuvarna.bg.persistence.repository.QuizRepository;
import sit.tuvarna.bg.persistence.repository.UserRepository;
import sit.tuvarna.bg.persistence.repository.UsersQuizzesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class QuizServiceTest {

    private static final String QUIZ_ID = UUID.randomUUID().toString();
    private static final String USER_EMAIL = "test@example.com";
    private static final String CATEGORY_NAME = "Test Category";
    private static final String QUIZ_TITLE = "Test Quiz";
    private static final String CREATOR_EMAIL = "test@example.com";
    private static final String QUESTION_CONTENT = "Test Question";
    private static final String ANSWER_CONTENT = "Test Answer";

    @Mock
    private QuizRepository quizRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private UsersQuizzesRepository usersQuizzesRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ConversionService conversionService;
    @InjectMocks
    private CreateQuizOperationProcessor createQuizOperationProcessor;
    @InjectMocks
    private ActiveQuizOperationProcessor activeQuizOperationProcessor;
    @InjectMocks
    private GetAllQuizzesForUserForUserOperationProcessor getAllQuizzesForUserForUserOperationProcessor;
    @InjectMocks
    private GetCompletedQuizzesOperationProcessor getCompletedQuizzesOperationProcessor;
    @InjectMocks
    private SolveQuizOperationProcessor solveQuizOperationProcessor;
    @InjectMocks
    private GetQuizByIdOperationProcessor getQuizByIdOperationProcessor;
    @InjectMocks
    private GetLeaderboardOperationProcessor getLeaderboardOperationProcessor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateQuizSuccess() {
        CreateQuizRequest request = CreateQuizRequest.builder()
                .title("Test Quiz")
                .category("Test Category")
                .userEmail("test@example.com")
                .questions(List.of(/* add questions here */))
                .build();

        Category category = Category.builder()
                .category("Test Category")
                .build();

        when(categoryRepository.findByCategory("Test Category")).thenReturn(Optional.of(category));
        when(quizRepository.save(any(Quiz.class))).thenAnswer(invocation -> {
            Quiz quiz = invocation.getArgument(0);
            quiz.setId(UUID.fromString(QUIZ_ID));
            return quiz;
        });

        CreateQuizResponse response = createQuizOperationProcessor.process(request);

        assertNotNull(response);
        assertEquals(QUIZ_ID, response.getId());
        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    public void testCreateCategorySuccess() {
        CreateQuizRequest request = CreateQuizRequest.builder()
                .title("Test Quiz")
                .category("New Category")
                .userEmail("test@example.com")
                .questions(List.of(/* add questions here */))
                .build();

        when(categoryRepository.findByCategory("New Category")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(quizRepository.save(any(Quiz.class))).thenAnswer(invocation -> {
            Quiz quiz = invocation.getArgument(0);
            quiz.setId(UUID.fromString(QUIZ_ID));
            return quiz;
        });

        CreateQuizResponse response = createQuizOperationProcessor.process(request);

        assertNotNull(response);
        assertEquals(QUIZ_ID, response.getId());
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    public void testCreateQuizDatabaseException() {
        CreateQuizRequest request = CreateQuizRequest.builder()
                .title("Test Quiz")
                .category("Test Category")
                .userEmail("test@example.com")
                .questions(List.of(/* add questions here */))
                .build();

        when(categoryRepository.findByCategory("Test Category")).thenReturn(Optional.of(new Category()));
        when(quizRepository.save(any(Quiz.class))).thenThrow(new RuntimeException());

        assertThrows(DatabaseException.class, () -> createQuizOperationProcessor.process(request));
        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    public void testActivateQuizSuccess() {
        ActiveQuizRequest request = ActiveQuizRequest.builder()
                .id(QUIZ_ID)
                .build();

        Quiz quiz = Quiz.builder()
                .id(UUID.fromString(QUIZ_ID))
                .status(QuizStatus.REQUESTED)
                .build();

        when(quizRepository.findById(UUID.fromString(QUIZ_ID))).thenReturn(Optional.of(quiz));
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);

        ActiveQuizResponse response = activeQuizOperationProcessor.process(request);

        assertNotNull(response);
        assertEquals(QuizStatus.ACTIVE.name(), response.getStatus());
        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    public void testActivateQuizAlreadyActive() {
        ActiveQuizRequest request = ActiveQuizRequest.builder()
                .id(QUIZ_ID)
                .build();

        Quiz quiz = Quiz.builder()
                .id(UUID.fromString(QUIZ_ID))
                .status(QuizStatus.ACTIVE)
                .build();

        when(quizRepository.findById(UUID.fromString(QUIZ_ID))).thenReturn(Optional.of(quiz));

        assertThrows(QuizAlreadyActiveException.class, () -> activeQuizOperationProcessor.process(request));
        verify(quizRepository, never()).save(any(Quiz.class));
    }

    @Test
    public void testActivateQuizNotFound() {
        ActiveQuizRequest request = ActiveQuizRequest.builder()
                .id(QUIZ_ID)
                .build();

        when(quizRepository.findById(UUID.fromString(QUIZ_ID))).thenReturn(Optional.empty());

        assertThrows(QuizNotFoundException.class, () -> activeQuizOperationProcessor.process(request));
        verify(quizRepository, never()).save(any(Quiz.class));
    }

    @Test
    public void testGetAllQuizzesForUser_UserNotFound() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        GetAllQuizzesForUserRequest request = GetAllQuizzesForUserRequest.builder().userEmail(USER_EMAIL).build();

        assertThrows(UserNotFoundException.class, () -> getAllQuizzesForUserForUserOperationProcessor.process(request));
    }

    @Test
    public void testGetAllQuizzesForUser_NoQuizzes() {
        User user = User.builder().email(USER_EMAIL).build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(quizRepository.findAll()).thenReturn(new ArrayList<>());

        GetAllQuizzesForUserRequest request = GetAllQuizzesForUserRequest.builder().userEmail(USER_EMAIL).build();

        GetAllQuizzesForUserResponse response = getAllQuizzesForUserForUserOperationProcessor.process(request);

        assertNotNull(response);
        assertTrue(response.getQuizModels().isEmpty());
    }

    @Test
    public void testGetCompletedQuizzesSuccess() {
        User user = User.builder().email(USER_EMAIL).build();
        Category category = Category.builder().category(CATEGORY_NAME).build();
        Quiz quiz = Quiz.builder()
                .id(UUID.randomUUID())
                .title("Test Quiz")
                .category(category)
                .status(QuizStatus.ACTIVE)
                .build();
        UsersQuizzes usersQuizzes = UsersQuizzes.builder()
                .user(user)
                .quiz(quiz)
                .isTaken(true)
                .build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(usersQuizzesRepository.getUsersQuizzesByUser(user)).thenReturn(List.of(usersQuizzes));
        when(conversionService.convert(any(Quiz.class), eq(QuizModel.class))).thenAnswer(invocation -> {
            Quiz source = invocation.getArgument(0);
            return QuizModel.builder()
                    .quizId(source.getId().toString())
                    .name(source.getTitle())
                    .category(source.getCategory().getCategory())
                    .build();
        });

        GetCompletedQuizzesRequest request = GetCompletedQuizzesRequest.builder().email(USER_EMAIL).build();
        GetCompletedQuizzesResponse response = getCompletedQuizzesOperationProcessor.process(request);

        assertNotNull(response);
        assertEquals(1, response.getQuizzes().size());

        QuizModel quizModel = response.getQuizzes().get(0);
        assertEquals(quiz.getId().toString(), quizModel.getQuizId());
        assertEquals(quiz.getTitle(), quizModel.getName());
        assertEquals(quiz.getCategory().getCategory(), quizModel.getCategory());
    }

    @Test
    public void testGetCompletedQuizzes_UserNotFound() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        GetCompletedQuizzesRequest request = GetCompletedQuizzesRequest.builder().email(USER_EMAIL).build();

        assertThrows(UserNotFoundException.class, () -> getCompletedQuizzesOperationProcessor.process(request));
    }

    @Test
    public void testGetCompletedQuizzes_NoCompletedQuizzes() {
        User user = User.builder().email(USER_EMAIL).build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(usersQuizzesRepository.getUsersQuizzesByUser(user)).thenReturn(new ArrayList<>());

        GetCompletedQuizzesRequest request = GetCompletedQuizzesRequest.builder().email(USER_EMAIL).build();

        GetCompletedQuizzesResponse response = getCompletedQuizzesOperationProcessor.process(request);

        assertNotNull(response);
        assertTrue(response.getQuizzes().isEmpty());
    }

    @Test
    public void testGetCompletedQuizzes_ConversionException() {
        User user = User.builder().email(USER_EMAIL).build();
        Category category = Category.builder().category(CATEGORY_NAME).build();
        Quiz quiz = Quiz.builder()
                .id(UUID.randomUUID())
                .title("Test Quiz")
                .category(category)
                .status(QuizStatus.ACTIVE)
                .build();
        UsersQuizzes usersQuizzes = UsersQuizzes.builder()
                .user(user)
                .quiz(quiz)
                .isTaken(true)
                .build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(usersQuizzesRepository.getUsersQuizzesByUser(user)).thenReturn(List.of(usersQuizzes));
        when(conversionService.convert(any(Quiz.class), eq(QuizModel.class))).thenThrow(new RuntimeException("Conversion failed"));

        GetCompletedQuizzesRequest request = GetCompletedQuizzesRequest.builder().email(USER_EMAIL).build();

        assertThrows(RuntimeException.class, () -> getCompletedQuizzesOperationProcessor.process(request));
    }

    @Test
    public void testSolveQuiz_UserNotFound() {
        SolveQuizRequest request = SolveQuizRequest.builder()
                .email(USER_EMAIL)
                .quizId(QUIZ_ID)
                .correctAnswers(8)
                .secondsToSolve(200)
                .isDaily(false)
                .build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> solveQuizOperationProcessor.process(request));
    }

    @Test
    public void testSolveQuiz_QuizNotFound() {
        User user = User.builder().email(USER_EMAIL).build();
        SolveQuizRequest request = SolveQuizRequest.builder()
                .email(USER_EMAIL)
                .quizId(QUIZ_ID)
                .correctAnswers(8)
                .secondsToSolve(200)
                .isDaily(false)
                .build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(quizRepository.findById(UUID.fromString(QUIZ_ID))).thenReturn(Optional.empty());

        assertThrows(QuizNotFoundException.class, () -> solveQuizOperationProcessor.process(request));
    }

    @Test
    public void testGetQuizById_Success() {
        Answer answer = Answer.builder()
                .content(ANSWER_CONTENT)
                .isCorrect(true)
                .build();
        Question question = Question.builder()
                .question(QUESTION_CONTENT)
                .type(sit.tuvarna.bg.persistence.enums.QuestionType.SINGLE_ANSWER)
                .answers(List.of(answer))
                .build();
        answer.setQuestion(question);
        Category category = Category.builder().category(CATEGORY_NAME).build();
        Quiz quiz = Quiz.builder()
                .id(UUID.fromString(QUIZ_ID))
                .title(QUIZ_TITLE)
                .category(category)
                .creatorEmail(CREATOR_EMAIL)
                .questions(List.of(question))
                .isDaily(true)
                .build();
        question.setQuiz(quiz);

        when(quizRepository.findById(UUID.fromString(QUIZ_ID))).thenReturn(Optional.of(quiz));

        GetQuizByIdRequest request = GetQuizByIdRequest.builder()
                .quizId(QUIZ_ID)
                .build();

        GetQuizByIdResponse response = getQuizByIdOperationProcessor.process(request);

        assertNotNull(response);
        assertEquals(QUIZ_TITLE, response.getTitle());
        assertEquals(CATEGORY_NAME, response.getCategory());
        assertEquals(CREATOR_EMAIL, response.getUserEmail());
        assertTrue(response.getIsDaily());
        assertEquals(1, response.getQuestions().size());

        QuestionModel questionModel = response.getQuestions().get(0);
        assertEquals(QUESTION_CONTENT, questionModel.getQuestion());
        assertEquals(QuestionType.SINGLE_ANSWER, questionModel.getQuestionType());
        assertEquals(1, questionModel.getAnswers().size());

        AnswerModel answerModel = questionModel.getAnswers().get(0);
        assertEquals(ANSWER_CONTENT, answerModel.getContent());
        assertTrue(answerModel.getIsCorrect());
    }

    @Test
    public void testGetQuizById_QuizNotFound() {
        when(quizRepository.findById(UUID.fromString(QUIZ_ID))).thenReturn(Optional.empty());

        GetQuizByIdRequest request = GetQuizByIdRequest.builder()
                .quizId(QUIZ_ID)
                .build();

        assertThrows(QuizNotFoundException.class, () -> getQuizByIdOperationProcessor.process(request));
    }

    @Test
    public void testGetLeaderboard_Success() {
        User user1 = User.builder()
                .email("user1@example.com")
                .level(5)
                .achievementPoints(100)
                .build();

        User user2 = User.builder()
                .email("user2@example.com")
                .level(10)
                .achievementPoints(200)
                .build();

        Quiz quiz1 = Quiz.builder()
                .id(UUID.randomUUID())
                .build();

        Quiz quiz2 = Quiz.builder()
                .id(UUID.randomUUID())
                .build();

        UsersQuizzes uq1 = UsersQuizzes.builder()
                .user(user1)
                .quiz(quiz1)
                .build();

        UsersQuizzes uq2 = UsersQuizzes.builder()
                .user(user1)
                .quiz(quiz2)
                .build();

        UsersQuizzes uq3 = UsersQuizzes.builder()
                .user(user2)
                .quiz(quiz1)
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(usersQuizzesRepository.getUsersQuizzesByUser(user1)).thenReturn(List.of(uq1, uq2));
        when(usersQuizzesRepository.getUsersQuizzesByUser(user2)).thenReturn(List.of(uq3));

        GetLeaderboardRequest request = GetLeaderboardRequest.builder().build();
        GetLeaderboardResponse response = getLeaderboardOperationProcessor.process(request);

        assertNotNull(response);
        assertEquals(2, response.getLeaderboardModelList().size());

        LeaderboardModel model1 = response.getLeaderboardModelList().get(0);
        assertEquals("user1@example.com", model1.getUserEmail());
        assertEquals(5, model1.getLevel());
        assertEquals(100, model1.getAchievementPoints());
        assertEquals(2, model1.getSolvedQuizzesCount());

        LeaderboardModel model2 = response.getLeaderboardModelList().get(1);
        assertEquals("user2@example.com", model2.getUserEmail());
        assertEquals(10, model2.getLevel());
        assertEquals(200, model2.getAchievementPoints());
        assertEquals(1, model2.getSolvedQuizzesCount());
    }
}
