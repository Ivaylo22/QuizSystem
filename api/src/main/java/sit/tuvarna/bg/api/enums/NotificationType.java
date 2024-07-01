package sit.tuvarna.bg.api.enums;

public enum NotificationType {
    LEVEL_UP("Вдигнахте ниво %d!"),
    ACHIEVEMENT_EARNED("Честито, спечелихте значка \"%s\"!"),
    ACCESS_KEY("Генериран код за достъп: \"%s\""),
    TEST_RETURN("Тест \"%s\": Оценка \"%s\"");

    private final String messageFormat;

    NotificationType(String messageFormat) {
        this.messageFormat = messageFormat;
    }

    public String getMessage(Object... args) {
        return String.format(messageFormat, args);
    }
}
