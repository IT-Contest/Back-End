package ssuchaehwa.it_project.domain.quest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssuchaehwa.it_project.domain.quest.domain.entity.HashtagParty;
import ssuchaehwa.it_project.domain.quest.domain.entity.Party;

@Repository
public interface HashtagPartyRepository extends JpaRepository<HashtagParty, Long> {

    void deleteByParty(Party party);
}
