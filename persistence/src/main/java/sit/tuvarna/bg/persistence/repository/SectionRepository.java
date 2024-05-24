package sit.tuvarna.bg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.persistence.entity.Section;

import java.util.List;
import java.util.UUID;

@Repository
public interface SectionRepository extends JpaRepository<Section, UUID> {

    @Query("SELECT DISTINCT s FROM Section s " +
            "LEFT JOIN FETCH s.questions q " +
            "WHERE s.test.id = :testId")
    List<Section> findSectionsWithQuestionsByTestId(@Param("testId") UUID testId);

    @Query("SELECT DISTINCT s FROM Section s " +
            "LEFT JOIN FETCH s.questions q " +
            "WHERE s.test.accessKey = :accessKey")
    List<Section> findSectionsWithQuestionsByAccessKey(@Param("accessKey") String accessKey);
}
