package ssuchaehwa.it_project.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssuchaehwa.it_project.global.common.BaseTimeEntity;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor
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
}