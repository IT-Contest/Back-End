package ssuchaehwa.it_project.domain.quest.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import ssuchaehwa.it_project.domain.model.entity.BaseTimeEntity;
import ssuchaehwa.it_project.domain.model.enums.FriendStatus;
import ssuchaehwa.it_project.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitedFriend extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 친구 요청을 보낸 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    // 친구 요청을 받은 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    // 상태: PENDING, ACCEPTED, REJECTED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus status;
}