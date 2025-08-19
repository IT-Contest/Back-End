package ssuchaehwa.it_project.domain.pomodoro.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import ssuchaehwa.it_project.domain.model.entity.BaseTimeEntity;
import ssuchaehwa.it_project.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "FocusSession",
    indexes = { @Index(name = "idx_pomodoro_user_start", columnList = "user_id,start_time") }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pomodoro extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "reward_exp")
    private int rewardExp;

    @Column(name = "reward_gold")
    private int rewardGold;

    // 편의 메서드: 뽀모도로 세션 지속 시간(분)
    public long getDurationMinutes() {
        if (startTime == null || endTime == null) return 0L;
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }
}