package sit.tuvarna.bg.persistence.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notifications")
@Data
@Getter
@Setter
@Builder
public class Notification {
    @Id
    private String id;
    private String email;
    private String message;
    private boolean read;
    private Instant createdAt;
}
