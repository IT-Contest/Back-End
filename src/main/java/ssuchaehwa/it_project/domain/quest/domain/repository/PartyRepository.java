package ssuchaehwa.it_project.domain.quest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssuchaehwa.it_project.domain.model.enums.CompletionStatus;
import ssuchaehwa.it_project.domain.quest.domain.entity.Party;
import ssuchaehwa.it_project.domain.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface PartyRepository extends JpaRepository<Party, Long> {

    void deleteByUser(User user);

    List<Party> findAllByCompletionStatusAndExpiresAtBefore(CompletionStatus status, LocalDateTime now);

    // 내가 만든 파티
    List<Party> findAllByUserId(Long userId);

    // 내가 속한 파티
    // PartyRepository.java
    @Query("SELECT pu.party FROM PartyUser pu WHERE pu.user.id = :userId AND pu.invitationStatus = 'ACCEPTED'")
    List<Party> findAllByMemberUserId(Long userId);

}
