package ssuchaehwa.it_project.domain.quest.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import ssuchaehwa.it_project.domain.model.entity.BaseTimeEntity;
import ssuchaehwa.it_project.domain.model.enums.FriendStatus;
import ssuchaehwa.it_project.domain.user.domain.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InvitedFriend extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 친구 초대를 보낸 사람 (초대자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    // 친구 초대를 받은 사람 (수락자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id")
    private User toUser;

    // 초대 토큰 (링크 기반 친구 추가용)
    @Column(nullable = false)
    private String token;

    // 상태: PENDING, ACCEPTED, REJECTED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus status;

    // 초대 만료 시간
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public void accept(User toUser) {
        this.toUser = toUser;
        this.status = FriendStatus.ACCEPTED;
    }

    public void reject(User toUser) {
        this.toUser = toUser;
        this.status = FriendStatus.REJECTED;
    }
}