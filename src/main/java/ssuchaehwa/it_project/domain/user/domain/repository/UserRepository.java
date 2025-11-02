package ssuchaehwa.it_project.domain.user.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssuchaehwa.it_project.domain.user.domain.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialId(String socialId);

    boolean existsBySocialId(String socialId);

    boolean existsByNickname(String nickname);

    Optional<User> findByInviteCode(String inviteCode);

}