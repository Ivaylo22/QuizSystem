package sit.tuvarna.bg.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "question_attempt")
public class QuestionAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UsersTests usersTests;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    private Double pointsAwarded;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> chosenAnswers;
}
