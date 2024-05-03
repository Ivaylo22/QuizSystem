package sit.tuvarna.bg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.persistence.entity.Test;

import java.util.UUID;

@Repository
public interface TestRepository extends JpaRepository<Test, UUID> {
}
