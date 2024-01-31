package sit.tuvarna.bg.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import sit.tuvarna.bg.persistence.enums.QuizCategory;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "quizzes")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private QuizCategory category;

    @Column(nullable = false)
    private Boolean isActive;
}
