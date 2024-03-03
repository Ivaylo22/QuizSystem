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
@Table(name = "quizzes")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Boolean isRequested;

    @Column(nullable = false)
    private String creatorEmail;

    private Double averageCorrectAnswers;

    private Double averageTimeNeeded;

    @OneToMany
    List<Question> questions;

    @ManyToOne
    Category category;
}
