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
import sit.tuvarna.bg.api.operations.user.archive.ArchiveUserOperation;
import sit.tuvarna.bg.api.operations.user.archive.ArchiveUserRequest;
import sit.tuvarna.bg.api.operations.user.archive.ArchiveUserResponse;
import sit.tuvarna.bg.api.operations.user.changepassword.ChangePasswordOperation;
import sit.tuvarna.bg.api.operations.user.changepassword.ChangePasswordRequest;
import sit.tuvarna.bg.api.operations.user.changepassword.ChangePasswordResponse;
import sit.tuvarna.bg.api.operations.user.getinfo.GetUserInfoOperation;
import sit.tuvarna.bg.api.operations.user.getinfo.GetUserInfoRequest;
import sit.tuvarna.bg.api.operations.user.getinfo.GetUserInfoResponse;
import sit.tuvarna.bg.api.operations.user.login.LoginOperation;
import sit.tuvarna.bg.api.operations.user.login.LoginRequest;
import sit.tuvarna.bg.api.operations.user.login.LoginResponse;
import sit.tuvarna.bg.api.operations.user.register.RegisterOperation;
import sit.tuvarna.bg.api.operations.user.register.RegisterRequest;
import sit.tuvarna.bg.api.operations.user.register.RegisterResponse;
import sit.tuvarna.bg.core.externalservices.XPProgress;
import sit.tuvarna.bg.core.externalservices.XPProgressService;
import sit.tuvarna.bg.core.processor.user.LogoutService;
import sit.tuvarna.bg.rest.QuizApplication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = QuizApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginOperation login;

    @MockBean
    private RegisterOperation register;

    @MockBean
    private LogoutService logout;

    @MockBean
    private GetUserInfoOperation getUserInfo;

    @MockBean
    private ChangePasswordOperation changePassword;

    @MockBean
    private ArchiveUserOperation archiveUser;

    @MockBean
    private XPProgressService progressService;

    private static final String GET_USER_INFO = "/api/v1/user/info";
    private static final String GET_XP_INFO = "/api/v1/user/xp-info";
    private static final String REGISTER_USER = "/api/v1/user/register";
    private static final String LOGIN_USER = "/api/v1/user/login";
    private static final String LOGOUT_USER = "/api/v1/user/logout";
    private static final String CHANGE_PASSWORD = "/api/v1/user/change-password";
    private static final String ARCHIVE_USER = "/api/v1/user/archive-user";

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        Mockito.reset(login, register, logout, getUserInfo, changePassword, archiveUser, progressService);
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetUserInfo() throws Exception {
        GetUserInfoResponse response = new GetUserInfoResponse();
        GetUserInfoRequest request = GetUserInfoRequest.builder().email("test@example.com").build();
        when(getUserInfo.process(any(GetUserInfoRequest.class))).thenReturn(response);

        mockMvc.perform(get(GET_USER_INFO)
                        .param("email", "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testGetXPProgress() throws Exception {
        XPProgress xpProgress = new XPProgress(100);
        when(progressService.calculateXPProgress(any(Integer.class))).thenReturn(xpProgress);

        mockMvc.perform(get(GET_XP_INFO)
                        .param("totalXp", "100")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(xpProgress)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testRegisterUser() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .password("Parola123,!")
                .confirmPassword("Parola123,!")
                .build();
        RegisterResponse response = new RegisterResponse();
        when(register.process(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post(REGISTER_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testLoginUser() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("Parola123,!")
                .build();
        LoginResponse response = new LoginResponse();
        when(login.process(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post(LOGIN_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testLogoutUser() throws Exception {
        mockMvc.perform(post(LOGOUT_USER)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testChangePassword() throws Exception {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .email("test@example.com")
                .password("Parola123,!")
                .confirmPassword("Parola123,!")
                .build();
        ChangePasswordResponse response = new ChangePasswordResponse();
        when(changePassword.process(any(ChangePasswordRequest.class))).thenReturn(response);

        mockMvc.perform(patch(CHANGE_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    public void testArchiveUser() throws Exception {
        ArchiveUserRequest request = ArchiveUserRequest.builder()
                .email("test@example.com")
                .build();
        ArchiveUserResponse response = new ArchiveUserResponse();
        when(archiveUser.process(any(ArchiveUserRequest.class))).thenReturn(response);

        mockMvc.perform(patch(ARCHIVE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}
