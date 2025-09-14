package ssuchaehwa.it_project.domain.user.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssuchaehwa.it_project.domain.user.domain.entity.Term;
import ssuchaehwa.it_project.domain.user.domain.entity.User;
import ssuchaehwa.it_project.domain.user.domain.entity.UserTerm;

import java.util.List;

@Repository
public interface UserTermRepository extends JpaRepository<UserTerm, Long> {

    boolean existsByUserAndTerm(User user, Term term);
    List<UserTerm> findByUser(User user);
    void deleteByUser(User user);
}
