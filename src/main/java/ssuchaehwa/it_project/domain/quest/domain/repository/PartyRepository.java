package ssuchaehwa.it_project.domain.quest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssuchaehwa.it_project.domain.quest.domain.entity.Party;
import ssuchaehwa.it_project.domain.user.entity.User;

public interface PartyRepository extends JpaRepository<Party, Long> {

    void deleteByUser(User user);

}
