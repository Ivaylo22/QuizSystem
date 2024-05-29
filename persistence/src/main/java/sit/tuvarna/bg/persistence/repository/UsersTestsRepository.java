package sit.tuvarna.bg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.persistence.entity.Test;
import sit.tuvarna.bg.persistence.entity.User;
import sit.tuvarna.bg.persistence.entity.UsersTests;

import java.util.List;
import java.util.UUID;

@Repository
public interface UsersTestsRepository extends JpaRepository<UsersTests, UUID> {
    List<UsersTests> findAllByTest(Test test);

    boolean existsByUserAndTest(User user, Test test);
}
