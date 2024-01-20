package sit.tuvarna.bg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.tuvarna.bg.persistence.entity.Achievement;

import java.util.UUID;

public interface AchievementRepository extends JpaRepository<Achievement, Integer> {
}
