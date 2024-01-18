package sit.tuvarna.bg.core.converter.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sit.tuvarna.bg.api.operations.user.register.RegisterResponse;
import sit.tuvarna.bg.persistence.entity.User;

@Component
public class UserToRegisterUserResponse implements Converter<User, RegisterResponse> {
    @Override
    public RegisterResponse convert(User source) {
        return RegisterResponse
                .builder()
                .username(source.getUsername())
                .email(source.getEmail())
                .build();
    }
}
