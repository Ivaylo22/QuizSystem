package sit.tuvarna.bg.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "users_quizzes")
public class UsersQuizzes {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private Integer correctAnswers;

    @Column(nullable = false)
    private Integer secondsToSolve;

    @Column(nullable = false)
    private Boolean isTaken;

    @Column(nullable = false)
    private Boolean isPassed;

    @Column(nullable = false)
    private Integer experienceGained;

    @CreationTimestamp
    @Column(name = "solved_at", nullable = false, updatable = false)
    private LocalDateTime solvedAt;

}
