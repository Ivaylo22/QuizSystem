package sit.tuvarna.bg.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import sit.tuvarna.bg.persistence.deserializer.SubjectDeserializer;
import sit.tuvarna.bg.persistence.enums.TestStatus;

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
@Table(name = "tests")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"title", "grade", "subject", "hasMixedQuestions", "minutesToSolve", "status", "scoringFormula", "sections", "creatorEmail"})
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonIgnore
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer grade;

    @JsonProperty("hasMixedQuestions")
    private Boolean mixedQuestions = false;

    @Column(nullable = false)
    private String creatorEmail;

    private String accessKey;

    private Integer minutesToSolve = 40;

    @Column(nullable = false)
    private TestStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JacksonXmlElementWrapper(localName = "sections")
    private List<Section> sections = new ArrayList<>();

    private String scoringFormula = "formula1";

    @ManyToOne
    @JsonDeserialize(using = SubjectDeserializer.class)
    Subject subject;
}
