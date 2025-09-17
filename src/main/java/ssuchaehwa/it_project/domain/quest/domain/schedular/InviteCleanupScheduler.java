package ssuchaehwa.it_project.domain.quest.domain.schedular;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ssuchaehwa.it_project.domain.quest.domain.repository.InvitedFriendRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InviteCleanupScheduler {

    private final InvitedFriendRepository invitedFriendRepository;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void cleanupExpiredInvites() {
        invitedFriendRepository.deleteExpiredPendingInvites(LocalDateTime.now());
    }
}
