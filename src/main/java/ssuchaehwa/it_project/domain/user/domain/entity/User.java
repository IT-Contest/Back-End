package ssuchaehwa.it_project.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ssuchaehwa.it_project.domain.model.entity.BaseTimeEntity;

@Entity
@Table(name = "user")
@Getter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "social_id", nullable = false, unique = true)
    private String socialId;

    private String nickname;

    private int level;

    private int exp;

    private int gold;

    private int diamond;

    @Column(name = "onboarding_completed")
    private boolean onboardingCompleted;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    // ✅ 초대 코드 추가!
    @Column(name = "invite_code", unique = true, updatable = false, nullable = false)
    private String inviteCode;

    // 보상 지급 시 유저의 경험치 증가
    // addExp(amount) 형태로 호출
    public void addExp(int amount) {
        this.exp += amount;
    }

    public void addGold(int amount) {
        this.gold += amount;
    }

    // 경험치 차감 (0 미만으로 가지 않도록)
    public void deductExp(int amount) {
        this.exp = Math.max(0, this.exp - amount);
    }

    // 골드 차감 (0 미만으로 가지 않도록)
    public void deductGold(int amount) {
        this.gold = Math.max(0, this.gold - amount);
    }
}