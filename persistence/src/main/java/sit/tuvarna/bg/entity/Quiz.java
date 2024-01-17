package sit.tuvarna.bg.entity;

import jakarta.persistence.*;
import lombok.*;
import sit.tuvarna.bg.enums.QuizCategory;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "quizzes")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer maxExperience;

    @Enumerated(EnumType.STRING)
    private QuizCategory category;
}
