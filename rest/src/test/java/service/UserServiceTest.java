package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import sit.tuvarna.bg.api.exception.*;
import sit.tuvarna.bg.api.operations.user.archive.ArchiveUserRequest;
import sit.tuvarna.bg.api.operations.user.archive.ArchiveUserResponse;
import sit.tuvarna.bg.api.operations.user.changepassword.ChangePasswordRequest;
import sit.tuvarna.bg.api.operations.user.changepassword.ChangePasswordResponse;
import sit.tuvarna.bg.api.operations.user.getinfo.GetUserInfoRequest;
import sit.tuvarna.bg.api.operations.user.getinfo.GetUserInfoResponse;
import sit.tuvarna.bg.api.operations.user.login.LoginRequest;
import sit.tuvarna.bg.api.operations.user.login.LoginResponse;
import sit.tuvarna.bg.api.operations.user.register.RegisterRequest;
import sit.tuvarna.bg.api.operations.user.register.RegisterResponse;
import sit.tuvarna.bg.core.externalservices.JwtService;
import sit.tuvarna.bg.core.processor.user.*;
import sit.tuvarna.bg.persistence.entity.Token;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.repository.TokenRepository;
import sit.tuvarna.bg.persistence.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private static final String USER_EMAIL = "test@example.com";
    private static final String PASSWORD = "password";
    private static final String TOKEN = UUID.randomUUID().toString();

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private ArchiveUserOperationProcessor archiveUserOperationProcessor;

    @InjectMocks
    private ChangePasswordOperationProcessor changePasswordOperationProcessor;

    @InjectMocks
    private GetUserInfoOperationProcessor getUserInfoOperationProcessor;

    @InjectMocks
    private LoginOperationProcessor loginOperationProcessor;

    @InjectMocks
    private RegisterOperationProcessor registerOperationProcessor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testArchiveUser_Success() {
        User user = User.builder().email(USER_EMAIL).build();
        ArchiveUserRequest request = ArchiveUserRequest.builder().email(USER_EMAIL).build();
        ArchiveUserResponse expectedResponse = new ArchiveUserResponse();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(conversionService.convert(user, ArchiveUserResponse.class)).thenReturn(expectedResponse);

        ArchiveUserResponse response = archiveUserOperationProcessor.process(request);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testArchiveUser_UserNotFound() {
        ArchiveUserRequest request = ArchiveUserRequest.builder().email(USER_EMAIL).build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> archiveUserOperationProcessor.process(request));
    }

    @Test
    public void testChangePassword_Success() {
        User user = User.builder().email(USER_EMAIL).build();
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .email(USER_EMAIL)
                .password(PASSWORD)
                .confirmPassword(PASSWORD)
                .build();
        ChangePasswordResponse expectedResponse = new ChangePasswordResponse();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(PASSWORD)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(conversionService.convert(user, ChangePasswordResponse.class)).thenReturn(expectedResponse);

        ChangePasswordResponse response = changePasswordOperationProcessor.process(request);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testChangePassword_PasswordsDoNotMatch() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .email(USER_EMAIL)
                .password(PASSWORD)
                .confirmPassword("differentPassword")
                .build();

        assertThrows(UserNotFoundException.class, () -> changePasswordOperationProcessor.process(request));
    }

    @Test
    public void testGetUserInfo_Success() {
        User user = User.builder().email(USER_EMAIL).build();
        GetUserInfoRequest request = GetUserInfoRequest.builder().email(USER_EMAIL).build();
        GetUserInfoResponse expectedResponse = new GetUserInfoResponse();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(conversionService.convert(user, GetUserInfoResponse.class)).thenReturn(expectedResponse);

        GetUserInfoResponse response = getUserInfoOperationProcessor.process(request);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
    }

    @Test
    public void testGetUserInfo_UserNotFound() {
        GetUserInfoRequest request = GetUserInfoRequest.builder().email(USER_EMAIL).build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> getUserInfoOperationProcessor.process(request));
    }

    @Test
    public void testLogin_Success() {
        User user = User.builder().email(USER_EMAIL).build();
        LoginRequest request = LoginRequest.builder().email(USER_EMAIL).password(PASSWORD).build();
        LoginResponse expectedResponse = LoginResponse.builder().email(USER_EMAIL).token(TOKEN).build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(TOKEN);
        when(userRepository.save(any(User.class))).thenReturn(user);

        LoginResponse response = loginOperationProcessor.process(request);

        assertNotNull(response);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    public void testLogin_UserNotFound() {
        LoginRequest request = LoginRequest.builder().email(USER_EMAIL).password(PASSWORD).build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> loginOperationProcessor.process(request));
    }

    @Test
    public void testRegister_Success() {
        RegisterRequest request = RegisterRequest.builder()
                .email(USER_EMAIL)
                .password(PASSWORD)
                .confirmPassword(PASSWORD)
                .build();
        RegisterResponse expectedResponse = new RegisterResponse();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(PASSWORD)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(User.builder().email(USER_EMAIL).build());
        when(conversionService.convert(any(User.class), eq(RegisterResponse.class))).thenReturn(expectedResponse);

        RegisterResponse response = registerOperationProcessor.process(request);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegister_UserExists() {
        RegisterRequest request = RegisterRequest.builder().email(USER_EMAIL).build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(new User()));

        assertThrows(UserExistsException.class, () -> registerOperationProcessor.process(request));
    }

    @Test
    public void testRegister_PasswordsDoNotMatch() {
        RegisterRequest request = RegisterRequest.builder()
                .email(USER_EMAIL)
                .password(PASSWORD)
                .confirmPassword("differentPassword")
                .build();

        assertThrows(PasswordsDoNotMatchException.class, () -> registerOperationProcessor.process(request));
    }
}
