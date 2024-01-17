package sit.tuvarna.bg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.tuvarna.bg.entity.Achievement;

import java.util.UUID;

public interface AchievementRepository extends JpaRepository<Achievement, UUID> {
}
