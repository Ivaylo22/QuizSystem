package sit.tuvarna.bg.core.processor.user;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.exception.UserNotFoundException;
import sit.tuvarna.bg.api.operations.user.getinfo.GetUserInfoOperation;
import sit.tuvarna.bg.api.operations.user.getinfo.GetUserInfoRequest;
import sit.tuvarna.bg.api.operations.user.getinfo.GetUserInfoResponse;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class GetUserInfoOperationProcessor implements GetUserInfoOperation {
    private final UserRepository userRepository;
    private final ConversionService conversionService;

    @Override
    public GetUserInfoResponse process(GetUserInfoRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);

        return conversionService.convert(user, GetUserInfoResponse.class);
    }
}
