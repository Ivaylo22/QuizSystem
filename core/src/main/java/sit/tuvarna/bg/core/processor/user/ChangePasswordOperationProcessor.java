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

        if (!Objects.equals(request.getPassword(), request.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException();
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return conversionService.convert(user, ChangePasswordResponse.class);
    }
}
