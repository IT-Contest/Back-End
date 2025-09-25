package ssuchaehwa.it_project.domain.quest.domain.schedular;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ssuchaehwa.it_project.domain.model.enums.CompletionStatus;
import ssuchaehwa.it_project.domain.model.enums.InvitationStatus;
import ssuchaehwa.it_project.domain.quest.domain.entity.Party;
import ssuchaehwa.it_project.domain.quest.domain.entity.PartyUser;
import ssuchaehwa.it_project.domain.quest.domain.repository.InvitedFriendRepository;
import ssuchaehwa.it_project.domain.quest.domain.repository.PartyRepository;
import ssuchaehwa.it_project.domain.quest.domain.repository.PartyUserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InviteCleanupScheduler {

    private final InvitedFriendRepository invitedFriendRepository;
    private final PartyUserRepository partyUserRepository;
    private final PartyRepository partyRepository;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void cleanupExpiredInvites() {
        LocalDateTime now = LocalDateTime.now();

        // ✅ 초대 만료된 PartyUser 정리 (expiresAt < now && status = PENDING → DECLINED 처리 or 삭제)
        invitedFriendRepository.deleteExpiredPendingInvites(now);
        partyUserRepository.deleteExpiredInvites(now);

        // ✅ 모든 Party를 조회해서 상태 갱신
        List<Party> parties = partyRepository.findAllByCompletionStatus(CompletionStatus.INCOMPLETE);

        for (Party party : parties) {
            List<PartyUser> partyUsers = party.getPartyUsers();

            boolean allAccepted = partyUsers.stream()
                    .allMatch(pu -> pu.getInvitationStatus() == InvitationStatus.ACCEPTED);

            boolean anyAccepted = partyUsers.stream()
                    .anyMatch(pu -> pu.getInvitationStatus() == InvitationStatus.ACCEPTED);

            boolean allResponded = partyUsers.stream()
                    .allMatch(pu -> pu.getInvitationStatus() == InvitationStatus.ACCEPTED
                            || pu.getInvitationStatus() == InvitationStatus.DECLINED);

            if (allAccepted) {
                // 모두 수락 → IN_PROGRESS
                party.changeCompletionStatus(CompletionStatus.IN_PROGRESS);
                partyRepository.save(party);
            } else if (anyAccepted && allResponded) {
                // 일부 수락 + 나머지 거절 → IN_PROGRESS
                party.changeCompletionStatus(CompletionStatus.IN_PROGRESS);
                partyRepository.save(party);
            } else if (!anyAccepted && allResponded) {
                // 모두 거절 → 파티 삭제
                partyUserRepository.deleteByParty(party);
                partyRepository.delete(party);
            }
        }
    }
}
