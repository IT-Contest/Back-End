package ssuchaehwa.it_project.domain.pomodoro.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import ssuchaehwa.it_project.domain.model.entity.BaseTimeEntity;
import ssuchaehwa.it_project.domain.user.domain.entity.User;
import ssuchaehwa.it_project.domain.pomodoro.exception.PomodoroException;
import ssuchaehwa.it_project.global.error.code.status.ErrorStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;

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

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PomodoroStatus status;

    @Column(name = "reward_exp")
    private int rewardExp;

    @Column(name = "reward_gold")
    private int rewardGold;

    // 편의 메서드: 뽀모도로 세션 지속 시간(분)
    public long getDurationMinutes() {
        if (startTime == null || endTime == null) return 0L;
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    // 뽀모도로 완료 처리
    public void complete() {
        if (this.status != PomodoroStatus.IN_PROGRESS) {
            throw new PomodoroException(ErrorStatus.POMODORO_ALREADY_PROCESSED);
        }
        this.status = PomodoroStatus.COMPLETED;
        this.endTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        this.rewardExp = 10;
        this.rewardGold = 5;
    }

    // 뽀모도로 취소 처리
    public void cancel() {
        if (this.status != PomodoroStatus.IN_PROGRESS) {
            throw new PomodoroException(ErrorStatus.POMODORO_ALREADY_PROCESSED);
        }
        this.status = PomodoroStatus.CANCELED;
        this.endTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}