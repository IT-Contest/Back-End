package ssuchaehwa.it_project.domain.pomodoro.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import ssuchaehwa.it_project.domain.model.entity.BaseTimeEntity;
import ssuchaehwa.it_project.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "FocusSession")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pomodoro extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "reward_exp")
    private int rewardExp;

    @Column(name = "reward_gold")
    private int rewardGold;
}