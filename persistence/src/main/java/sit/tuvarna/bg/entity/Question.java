package sit.tuvarna.bg.entity;

import jakarta.persistence.*;
import lombok.*;
import sit.tuvarna.bg.enums.QuestionType;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @ManyToOne
    private Quiz quiz;
}
