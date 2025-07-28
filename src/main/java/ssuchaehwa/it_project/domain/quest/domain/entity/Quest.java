package ssuchaehwa.it_project.domain.quest.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssuchaehwa.it_project.domain.model.entity.BaseTimeEntity;
import ssuchaehwa.it_project.domain.model.enums.CompletionStatus;
import ssuchaehwa.it_project.domain.model.enums.QuestType;
import ssuchaehwa.it_project.domain.user.entity.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Quest extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 퀘스트 내용
    @Column(nullable = false, length = 100)
    private String title;

    // 경험치 보상
    private int expReward;

    // 골드 보상
    private int goldReward;

    // 우선 순위
    private int priority;

    // 퀘스트 완료, 미완료 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompletionStatus completionStatus;

    // 일일, 주간, 월간, 주간 구분
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestType questType;

    // 시작 시간과 종료 시간
    private LocalTime startTime;
    private LocalTime endTime;

    // 시작 날짜
    private LocalDate startDate;

    // 마감 날짜
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "quest", fetch = FetchType.LAZY)
    private Party party;

    @OneToMany(mappedBy = "quest", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HashtagQuest> hashtagQuests = new ArrayList<>();
}
