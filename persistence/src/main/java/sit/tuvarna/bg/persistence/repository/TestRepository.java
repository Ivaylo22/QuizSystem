package sit.tuvarna.bg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.persistence.entity.Test;
import sit.tuvarna.bg.persistence.enums.TestStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TestRepository extends JpaRepository<Test, UUID> {
    List<Test> getAllByStatus(TestStatus status);

    @Query("SELECT t FROM Test t WHERE t.id = :id")
    Optional<Test> findByIdBasic(UUID id);
}
