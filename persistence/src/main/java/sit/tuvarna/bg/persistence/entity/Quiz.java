package sit.tuvarna.bg.persistence.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import sit.tuvarna.bg.persistence.deserializer.CategoryDeserializer;
import sit.tuvarna.bg.persistence.enums.QuizStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "quizzes")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"title", "category", "creatorEmail", "questions"})
@JacksonXmlRootElement(localName = "quiz")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private QuizStatus status;

    @Column(nullable = false)
    @JsonProperty("creatorEmail")
    private String creatorEmail;

    @Column(nullable = false)
    @JsonProperty("isDaily")
    private Boolean isDaily = false;

    @JsonProperty("averageCorrectAnswers")
    private Double averageCorrectAnswers = 0.0;

    @JsonProperty("averageSecondsNeeded")
    private Integer averageSecondsNeeded = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Question> questions = new ArrayList<>();

    @ManyToOne
    @JsonDeserialize(using = CategoryDeserializer.class)
    Category category;

    private LocalDateTime lastUpdated;
}
