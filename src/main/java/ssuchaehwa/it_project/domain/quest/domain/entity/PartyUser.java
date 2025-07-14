package ssuchaehwa.it_project.domain.quest.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssuchaehwa.it_project.domain.model.entity.BaseTimeEntity;
import ssuchaehwa.it_project.domain.model.enums.InvitationStatus;
import ssuchaehwa.it_project.domain.user.entity.User;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartyUser extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus invitationStatus;
}
