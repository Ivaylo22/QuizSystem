package sit.tuvarna.bg.core.processor.user;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.PasswordsDoNotMatchException;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.api.operations.user.changepassword.ChangePasswordOperation;
import sit.tuvarna.bg.api.operations.user.changepassword.ChangePasswordRequest;
import sit.tuvarna.bg.api.operations.user.changepassword.ChangePasswordResponse;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.repository.UserRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChangePasswordOperationProcessor implements ChangePasswordOperation {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConversionService conversionService;

    @Override
    public ChangePasswordResponse process(ChangePasswordRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);

        if(!Objects.equals(request.getNewPassword(), request.getConfirmNewPassword())) {
            throw new PasswordsDoNotMatchException();
        }

        User changedUser = User
                .builder()
                .email(user.getEmail())
                .password(passwordEncoder.encode(request.getNewPassword()))
                .level(user.getLevel())
                .experience(user.getExperience())
                .achievementPoints(user.getAchievementPoints())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .isArchived(user.getIsArchived())
                .build();

        userRepository.save(changedUser);

        return conversionService.convert(changedUser, ChangePasswordResponse.class);
    }
}
