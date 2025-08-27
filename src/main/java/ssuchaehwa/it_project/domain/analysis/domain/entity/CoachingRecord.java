package ssuchaehwa.it_project.domain.analysis.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ssuchaehwa.it_project.domain.model.entity.BaseTimeEntity;
import ssuchaehwa.it_project.domain.user.entity.User;

import java.time.LocalDate;

@Entity
@Table(name = "coaching_record")
@Getter
@DynamicUpdate
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class CoachingRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 분석 타입 (DAILY, WEEKLY, MONTHLY, YEARLY)
    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_type", nullable = false, length = 20)
    private AnalysisType analysisType;

    // 분석 대상 (QUEST, POMODORO)
    @Enumerated(EnumType.STRING)
    @Column(name = "quest_or_pomodoro", nullable = false, length = 20)
    private QuestOrPomodoro questOrPomodoro;

    // 분석 진행일자
    @Column(name = "analysis_date", nullable = false)
    private LocalDate analysisDate;

    // AI 코칭 내용 (JSON 형태)
    @Column(name = "coaching_content", nullable = false, columnDefinition = "TEXT")
    private String coachingContent;

    // 분석 타입 enum
    public enum AnalysisType {
        DAILY, WEEKLY, MONTHLY, YEARLY
    }

    // 분석 대상 enum
    public enum QuestOrPomodoro {
        QUEST, POMODORO
    }

    // 코칭 내용 업데이트 메서드
    public void updateCoachingContent(String coachingContent) {
        this.coachingContent = coachingContent;
    }

    // 코칭 기록 생성 메서드 (정적 팩토리 메서드)
    public static CoachingRecord createCoachingRecord(
            User user, 
            AnalysisType analysisType, 
            QuestOrPomodoro questOrPomodoro,
            String coachingContent) {
        
        return CoachingRecord.builder()
                .user(user)
                .analysisType(analysisType)
                .questOrPomodoro(questOrPomodoro)
                .analysisDate(LocalDate.now())
                .coachingContent(coachingContent)
                .build();
    }
}
