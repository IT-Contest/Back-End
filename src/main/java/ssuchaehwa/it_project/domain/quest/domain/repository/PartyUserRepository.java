package ssuchaehwa.it_project.domain.quest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ssuchaehwa.it_project.domain.model.enums.InvitationStatus;
import ssuchaehwa.it_project.domain.quest.domain.entity.Party;
import ssuchaehwa.it_project.domain.quest.domain.entity.PartyUser;
import ssuchaehwa.it_project.domain.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PartyUserRepository extends JpaRepository<PartyUser, Long> {

    List<PartyUser> findAllByUserIdAndInvitationStatus(Long userId, InvitationStatus status);
    Optional<PartyUser> findByUserIdAndPartyId(Long userId, Long partyId);

    void deleteByUser(User user);
    void deleteByParty(Party party);

    @Modifying
    @Query("DELETE FROM PartyUser pu WHERE pu.invitationStatus = 'PENDING' AND pu.expiresAt < :now")
    void deleteExpiredInvites(LocalDateTime now);

    List<PartyUser> findAllByUserIdAndInvitationStatusIn(Long userId, List<InvitationStatus> statuses);

}
