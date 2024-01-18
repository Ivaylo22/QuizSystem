package sit.tuvarna.bg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.tuvarna.bg.persistence.entity.Token;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {

    @Query(value = """
        select t from Token t join User u
        on t.user.id = u.id
        where u.id = :id and (t.expired = false or t.revoked = false)
    """)
    List<Token> findAllValidTokenByUser(UUID id);

    Optional<Token> findByToken(String token);
}
