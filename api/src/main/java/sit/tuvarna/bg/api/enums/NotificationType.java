package sit.tuvarna.bg.api.enums;

public enum NotificationType {
    LEVEL_UP("Вдигнахте ниво %d!"),
    ACHIEVEMENT_EARNED("Честито, спечелихте награда \"%s\"!");

    private final String messageFormat;

    NotificationType(String messageFormat) {
        this.messageFormat = messageFormat;
    }

    public String getMessage(Object... args) {
        return String.format(messageFormat, args);
    }
}
