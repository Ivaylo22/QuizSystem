package sit.tuvarna.bg.core.converter.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sit.tuvarna.bg.api.operations.user.changepassword.ChangePasswordResponse;
import sit.tuvarna.bg.persistence.entity.User;

@Component
public class UserToChangePasswordResponse implements Converter<User, ChangePasswordResponse> {
    @Override
    public ChangePasswordResponse convert(User source) {
        return ChangePasswordResponse
                .builder()
                .newPassword(source.getPassword())
                .email(source.getEmail())
                .build();
    }
}
