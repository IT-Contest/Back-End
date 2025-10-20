package ssuchaehwa.it_project.domain.user.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssuchaehwa.it_project.domain.user.domain.entity.User;
import ssuchaehwa.it_project.domain.model.enums.SocialProvider;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialId(String socialId);
    
    Optional<User> findBySocialIdAndProvider(String socialId, SocialProvider provider);

    boolean existsBySocialId(String socialId);
    
    boolean existsBySocialIdAndProvider(String socialId, SocialProvider provider);

    Optional<User> findByInviteCode(String inviteCode);

}