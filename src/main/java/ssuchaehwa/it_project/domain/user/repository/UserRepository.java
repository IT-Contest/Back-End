package ssuchaehwa.it_project.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<ssuchaehwa.it_project.domain.user.entity.User, Long> {
    Optional<ssuchaehwa.it_project.domain.user.entity.User> findBySocialId(String socialId);

    boolean existsBySocialId(String socialId);
}