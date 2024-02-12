package sit.tuvarna.bg.core.processor.user;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.Base64;
import java.util.Objects;

@Service
public class RegisterOperationProcessor implements RegisterOperation {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConversionService conversionService;

    @Autowired
    public RegisterOperationProcessor(UserRepository userRepository,
                                      PasswordEncoder passwordEncoder,
                                      ConversionService conversionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.conversionService = conversionService;
    }

    @Override
    public RegisterResponse process(RegisterRequest request) {
        userRepository
                .findByEmail(request.getEmail())
                .ifPresent(e -> {
                    throw new UserExistsException();
                });

        userRepository
                .findByUsername(request.getUsername())
                .ifPresent(e -> {
                    throw new UserExistsException();
                });

        if (!Objects.equals(request.getPassword(), request.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException();
        }

        Role role = userRepository.count() == 0 ? Role.ADMIN : Role.USER;

        User user = User
                .builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .level(1)
                .experience(0)
                .achievementPoints(0)
                .avatar(request.getAvatarData())
                .role(role)
                .isArchived(false)
                .quizzesUnderOneMinuteCount(0)
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
