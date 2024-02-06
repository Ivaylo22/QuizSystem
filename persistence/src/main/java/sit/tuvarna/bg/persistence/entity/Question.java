package sit.tuvarna.bg.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import sit.tuvarna.bg.persistence.enums.QuestionType;

import java.util.Base64;
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
    private String question;

    @Lob
    @Column
    private byte[] image;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @OneToMany
    private List<Answer> answers;

    public String getQuestionImage() {
        return Base64.getEncoder().encodeToString(image);
    }
}
