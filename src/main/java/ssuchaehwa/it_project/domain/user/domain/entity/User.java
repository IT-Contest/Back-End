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

    // 경험치 추가 후 레벨 자동 계산
    public void addExpAndUpdateLevel(int amount) {
        this.exp += amount;
        this.level = calculateLevelFromExp(this.exp);
    }

    // 골드 추가 후 레벨 자동 계산 (경험치 변경이 있을 때만)
    public void addGoldAndUpdateLevel(int amount) {
        this.gold += amount;
        // 골드 추가는 레벨에 영향 없음
    }

    // 경험치를 기반으로 레벨 계산
    public static int calculateLevelFromExp(int exp) {
        // 레벨별 누적 경험치 테이블 (0~100레벨)
        int[] levelThresholds = {
            0,     // 레벨 0: 0 exp
            100,   // 레벨 1: 100 exp
            110,   // 레벨 2: 110 exp
            140,   // 레벨 3: 140 exp
            190,   // 레벨 4: 190 exp
            260,   // 레벨 5: 260 exp
            350,   // 레벨 6: 350 exp
            460,   // 레벨 7: 460 exp
            590,   // 레벨 8: 590 exp
            740,   // 레벨 9: 740 exp
            910,   // 레벨 10: 910 exp
            1100,  // 레벨 11: 1100 exp
            1310,  // 레벨 12: 1310 exp
            1540,  // 레벨 13: 1540 exp
            1790,  // 레벨 14: 1790 exp
            2060,  // 레벨 15: 2060 exp
            2350,  // 레벨 16: 2350 exp
            2660,  // 레벨 17: 2660 exp
            2990,  // 레벨 18: 2990 exp
            3340,  // 레벨 19: 3340 exp
            3710,  // 레벨 20: 3710 exp
            4100,  // 레벨 21: 4100 exp
            4510,  // 레벨 22: 4510 exp
            4940,  // 레벨 23: 4940 exp
            5390,  // 레벨 24: 5390 exp
            5860,  // 레벨 25: 5860 exp
            6350,  // 레벨 26: 6350 exp
            6860,  // 레벨 27: 6860 exp
            7390,  // 레벨 28: 7390 exp
            7940,  // 레벨 29: 7940 exp
            8510,  // 레벨 30: 8510 exp
            9100,  // 레벨 31: 9100 exp
            9710,  // 레벨 32: 9710 exp
            10340, // 레벨 33: 10340 exp
            10990, // 레벨 34: 10990 exp
            11660, // 레벨 35: 11660 exp
            12350, // 레벨 36: 12350 exp
            13060, // 레벨 37: 13060 exp
            13790, // 레벨 38: 13790 exp
            14540, // 레벨 39: 14540 exp
            15310, // 레벨 40: 15310 exp
            16100, // 레벨 41: 16100 exp
            16910, // 레벨 42: 16910 exp
            17740, // 레벨 43: 17740 exp
            18590, // 레벨 44: 18590 exp
            19460, // 레벨 45: 19460 exp
            20350, // 레벨 46: 20350 exp
            21260, // 레벨 47: 21260 exp
            22190, // 레벨 48: 22190 exp
            23140, // 레벨 49: 23140 exp
            24110, // 레벨 50: 24110 exp
            25100, // 레벨 51: 25100 exp
            26110, // 레벨 52: 26110 exp
            27140, // 레벨 53: 27140 exp
            28190, // 레벨 54: 28190 exp
            29260, // 레벨 55: 29260 exp
            30350, // 레벨 56: 30350 exp
            31460, // 레벨 57: 31460 exp
            32590, // 레벨 58: 32590 exp
            33740, // 레벨 59: 33740 exp
            34910, // 레벨 60: 34910 exp
            36100, // 레벨 61: 36100 exp
            37310, // 레벨 62: 37310 exp
            38540, // 레벨 63: 38540 exp
            39790, // 레벨 64: 39790 exp
            41060, // 레벨 65: 41060 exp
            42350, // 레벨 66: 42350 exp
            43660, // 레벨 67: 43660 exp
            44990, // 레벨 68: 44990 exp
            46340, // 레벨 69: 46340 exp
            47710, // 레벨 70: 47710 exp
            49100, // 레벨 71: 49100 exp
            50510, // 레벨 72: 50510 exp
            51940, // 레벨 73: 51940 exp
            53390, // 레벨 74: 53390 exp
            54860, // 레벨 75: 54860 exp
            56350, // 레벨 76: 56350 exp
            57860, // 레벨 77: 57860 exp
            59390, // 레벨 78: 59390 exp
            60940, // 레벨 79: 60940 exp
            62510, // 레벨 80: 62510 exp
            64100, // 레벨 81: 64100 exp
            65710, // 레벨 82: 65710 exp
            67340, // 레벨 83: 67340 exp
            68990, // 레벨 84: 68990 exp
            70660, // 레벨 85: 70660 exp
            72350, // 레벨 86: 72350 exp
            74060, // 레벨 87: 74060 exp
            75790, // 레벨 88: 75790 exp
            77540, // 레벨 89: 77540 exp
            79310, // 레벨 90: 79310 exp
            81100, // 레벨 91: 81100 exp
            82910, // 레벨 92: 82910 exp
            84740, // 레벨 93: 84740 exp
            86590, // 레벨 94: 86590 exp
            88460, // 레벨 95: 88460 exp
            90350, // 레벨 96: 90350 exp
            92260, // 레벨 97: 92260 exp
            94190, // 레벨 98: 94190 exp
            96140, // 레벨 99: 96140 exp
            98110  // 레벨 100: 98110 exp
        };

        // 경험치에 맞는 레벨 찾기
        for (int level = levelThresholds.length - 1; level >= 0; level--) {
            if (exp >= levelThresholds[level]) {
                return level;
            }
        }
        return 0; // 기본값 (0 exp 미만인 경우)
    }

    // 현재 레벨 업데이트 메서드
    public void updateLevel() {
        this.level = calculateLevelFromExp(this.exp);
    }
    
    // 온보딩 완료 메서드
    public void completeOnboarding() {
        this.onboardingCompleted = true;
    }
}