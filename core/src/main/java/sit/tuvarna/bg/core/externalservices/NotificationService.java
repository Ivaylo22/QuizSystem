package sit.tuvarna.bg.core.externalservices;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.api.enums.NotificationType;
import sit.tuvarna.bg.persistence.repository.NotificationRepository;
import sit.tuvarna.bg.persistence.entity.Notification;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void createAndSendNotification(String email, NotificationType type, Object data) {
        String message = type.getMessage(data);
        Notification notification = Notification.builder()
                .email(email)
                .message(message)
                .read(false)
                .createdAt(Instant.now())
                .build();

        notification = notificationRepository.save(notification);
        messagingTemplate.convertAndSendToUser(email, "/topic/notifications", notification);
    }
}
