package sit.tuvarna.bg.core.converter.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sit.tuvarna.bg.api.model.UserModel;
import sit.tuvarna.bg.api.operations.user.getinfo.GetUserInfoRequest;
import sit.tuvarna.bg.api.operations.user.getinfo.GetUserInfoResponse;
import sit.tuvarna.bg.persistence.entity.User;

@Component
public class UserToGetUserInfoResponse implements Converter<User, GetUserInfoResponse> {
    @Override
    public GetUserInfoResponse convert(User source) {
        UserModel model = UserModel
                .builder()
                .username(source.getUsername())
                .email(source.getEmail())
                .avatar(source.getUserAvatar())
                .level(source.getLevel())
                .experience(source.getExperience())
                .achievementPoints(source.getAchievementPoints())
                .isArchived(source.getIsArchived())
                .quizzesUnderOneMinuteCount(source.getQuizzesUnderOneMinuteCount())
                .perfectQuizzesCount(source.getPerfectQuizzesCount())
                .consecutiveQuizzesPassedCount(source.getConsecutiveQuizzesPassedCount())
                .consecutiveDailyQuizzesCount(source.getConsecutiveDailyQuizzesCount())
                .dailyQuizzesCount(source.getDailyQuizzesCount())
                .quizzesPassedCount(source.getQuizzesPassedCount())
                .build();

        return GetUserInfoResponse
                .builder()
                .userInformation(model)
                .build();
    }
}
