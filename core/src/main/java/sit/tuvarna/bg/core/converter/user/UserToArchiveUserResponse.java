package sit.tuvarna.bg.core.converter.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sit.tuvarna.bg.api.operations.user.archive.ArchiveUserResponse;
import sit.tuvarna.bg.persistence.entity.User;

@Component
public class UserToArchiveUserResponse implements Converter<User, ArchiveUserResponse> {

    @Override
    public ArchiveUserResponse convert(User source) {
        return ArchiveUserResponse
                .builder()
                .email(source.getEmail())
                .isArchived(source.getIsArchived())
                .build();
    }
}
