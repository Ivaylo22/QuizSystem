package sit.tuvarna.bg.core.processor.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.api.operations.user.login.LoginOperation;
import sit.tuvarna.bg.api.operations.user.login.LoginRequest;
import sit.tuvarna.bg.api.operations.user.login.LoginResponse;
import sit.tuvarna.bg.core.externalservices.JwtService;
import sit.tuvarna.bg.persistence.entity.Token;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.enums.TokenType;
import sit.tuvarna.bg.persistence.repository.TokenRepository;
import sit.tuvarna.bg.persistence.repository.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginOperationProcessor implements LoginOperation {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    @Override
    public LoginResponse process(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        String token = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveToken(user, token);
        updateLastLogonTime(user);

        return LoginResponse
                .builder()
                .token(token)
                .email(user.getEmail())
                .build();

    }

    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if(validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveToken(User user, String token) {
        Token toPersist = Token
                .builder()
                .user(user)
                .token(token)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(toPersist);
    }

    private void updateLastLogonTime(User user) {
        user.setLastLoginTime(Timestamp.from(Instant.now()));
        userRepository.save(user);
    }
}
