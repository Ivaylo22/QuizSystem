package sit.tuvarna.bg.core.processor.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.api.operations.user.archive.ArchiveUserOperation;
import sit.tuvarna.bg.api.operations.user.archive.ArchiveUserRequest;
import sit.tuvarna.bg.api.operations.user.archive.ArchiveUserResponse;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArchiveUserOperationProcessor implements ArchiveUserOperation {
    private final UserRepository userRepository;
    private final ConversionService conversionService;

    @Override
    public ArchiveUserResponse process(ArchiveUserRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                        .orElseThrow(UserNotFoundException::new);

        user.setIsArchived(true);
        userRepository.save(user);

        return conversionService.convert(user, ArchiveUserResponse.class);
    }
}
