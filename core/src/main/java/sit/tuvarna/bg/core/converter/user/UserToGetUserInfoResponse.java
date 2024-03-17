package sit.tuvarna.bg.core.converter.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sit.tuvarna.bg.api.enums.Role;
import sit.tuvarna.bg.api.model.UserModel;
import sit.tuvarna.bg.api.operations.user.getinfo.GetUserInfoResponse;
import sit.tuvarna.bg.persistence.entity.User;

@Component
public class UserToGetUserInfoResponse implements Converter<User, GetUserInfoResponse> {
    @Override
    public GetUserInfoResponse convert(User source) {
        UserModel model = UserModel
                .builder()
                .email(source.getEmail())
                .avatarUrl(source.getAvatarUrl())
                .level(source.getLevel())
                .experience(source.getExperience())
                .achievementPoints(source.getAchievementPoints())
                .isArchived(source.getIsArchived())
                .quizzesUnderOneMinuteCount(source.getFastQuizzesCount())
                .perfectQuizzesCount(source.getPerfectQuizzesCount())
                .consecutiveQuizzesPassedCount(source.getConsecutiveQuizzesPassedCount())
                .consecutiveDailyQuizzesCount(source.getConsecutiveDailyQuizzesCount())
                .dailyQuizzesCount(source.getDailyQuizzesCount())
                .quizzesPassedCount(source.getQuizzesPassedCount())
                .role(Role.valueOf(source.getRole().name()))
                .build();

        return GetUserInfoResponse
                .builder()
                .userInformation(model)
                .build();
    }
}
