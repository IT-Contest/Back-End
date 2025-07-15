package ssuchaehwa.it_project.domain.quest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssuchaehwa.it_project.domain.model.enums.InvitationStatus;
import ssuchaehwa.it_project.domain.quest.domain.entity.PartyUser;

import java.util.List;
import java.util.Optional;

public interface PartyUserRepository extends JpaRepository<PartyUser, Long> {

    List<PartyUser> findAllByUserIdAndInvitationStatus(Long userId, InvitationStatus status);
    Optional<PartyUser> findByUserIdAndPartyId(Long userId, Long partyId);
}
