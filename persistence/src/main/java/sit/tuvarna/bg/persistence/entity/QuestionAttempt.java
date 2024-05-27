package sit.tuvarna.bg.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "question_attempt")
public class QuestionAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private UsersTests usersTests;

    @ManyToOne
    private Question question;

    private Double pointsAwarded;

    @ElementCollection
    private List<String> chosenAnswers;
}
