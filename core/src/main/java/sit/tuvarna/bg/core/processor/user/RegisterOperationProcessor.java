package sit.tuvarna.bg.core.processor.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.PasswordsDoNotMatchException;
import sit.tuvarna.bg.api.exception.UserExistsException;
import sit.tuvarna.bg.api.operations.user.register.RegisterOperation;
import sit.tuvarna.bg.api.operations.user.register.RegisterRequest;
import sit.tuvarna.bg.api.operations.user.register.RegisterResponse;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.enums.Role;
import sit.tuvarna.bg.persistence.repository.UserRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RegisterOperationProcessor implements RegisterOperation {

    @Value("${default-image}")
    private String defaultImageUrl;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConversionService conversionService;

    @Override
    @Transactional
    public RegisterResponse process(RegisterRequest request) {
        userRepository
                .findByEmail(request.getEmail())
                .ifPresent(e -> {
                    throw new UserExistsException();
                });

        if (!Objects.equals(request.getPassword(), request.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException();
        }

        Role role = userRepository.count() == 0 ? Role.ADMIN : Role.USER;

        User user = User
                .builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .level(1)
                .experience(0)
                .achievementPoints(0)
                .role(role)
                .isArchived(false)
                .avatarUrl(defaultImageUrl)
                .fastQuizzesCount(0)
                .perfectQuizzesCount(0)
                .consecutiveQuizzesPassedCount(0)
                .consecutiveDailyQuizzesCount(0)
                .dailyQuizzesCount(0)
                .quizzesPassedCount(0)
                .build();

        userRepository.save(user);

        return conversionService.convert(user, RegisterResponse.class);
    }
}
