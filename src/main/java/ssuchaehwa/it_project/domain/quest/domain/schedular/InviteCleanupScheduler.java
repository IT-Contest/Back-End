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

        // 친구 초대 만료
        invitedFriendRepository.deleteExpiredPendingInvites(now);
        partyUserRepository.deleteExpiredInvites(now);

        // 만료 시간이 지난 파티 조회 (아직 INCOMPLETE 상태인 것만)
        List<Party> expiredParties = partyRepository.findAllByCompletionStatusAndExpiresAtBefore(
                CompletionStatus.INCOMPLETE, now
        );

        for (Party party : expiredParties) {
            List<PartyUser> partyUsers = party.getPartyUsers();

            // 🔹 초대장이 하나도 없는 고아 파티 → 그냥 삭제
            if (partyUsers.isEmpty()) {
                partyRepository.delete(party);
                continue;
            }

            boolean allAccepted = partyUsers.stream().allMatch(pu -> pu.getInvitationStatus() == InvitationStatus.ACCEPTED);
            boolean anyAccepted = partyUsers.stream().anyMatch(pu -> pu.getInvitationStatus() == InvitationStatus.ACCEPTED);
            boolean allResponded = partyUsers.stream().allMatch(pu ->
                    pu.getInvitationStatus() == InvitationStatus.ACCEPTED ||
                            pu.getInvitationStatus() == InvitationStatus.DECLINED);

            if (allAccepted) {
                // 모두 수락 → 바로 진행
                party.changeCompletionStatus(CompletionStatus.IN_PROGRESS);
                partyRepository.save(party);
            } else if (anyAccepted && allResponded) {
                // 1명 이상 수락 + 나머지 거절 → 바로 진행
                party.changeCompletionStatus(CompletionStatus.IN_PROGRESS);
                partyRepository.save(party);
            } else if (!anyAccepted && allResponded) {
                // 모두 거절 → 삭제
                partyUserRepository.deleteByParty(party);
                partyRepository.delete(party);
            } else {
                // 일부 수락 + 일부 미응답 → 만료까지 기다리다가 지금 시점이면 처리
                if (party.getExpiresAt().isBefore(now)) {
                    if (anyAccepted) {
                        // 수락자 있으면 진행
                        party.changeCompletionStatus(CompletionStatus.IN_PROGRESS);
                        partyRepository.save(party);
                    } else {
                        // 수락자도 없으면 삭제
                        partyUserRepository.deleteByParty(party);
                        partyRepository.delete(party);
                    }
                }
            }
        }
    }
}