package ssuchaehwa.it_project.domain.analysis.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisDataDTO {
    
    // 분석 타입
    private String analysisType;
    
    // 분석 대상
    private String questOrPomodoro;
    
    // 퀘스트 관련 데이터
    private List<QuestDataDTO> quests;
    
    // 뽀모도로 관련 데이터
    private List<PomodoroDataDTO> pomodoros;
    
    // 분석 기간 (시작일 ~ 종료일)
    private String analysisPeriod;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestDataDTO {
        private Long questId;           // 퀘스트 ID
        private String title;           // 퀘스트명
        private Integer priority;       // 우선순위
        private String startTime;       // 시작시간
        private String endTime;         // 종료시간
        private String startDate;       // 시작일
        private String dueDate;         // 마감일
        private String completionStatus; // 완료여부
        private String questType;       // 분류 (일일/주간/월간/연간)
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PomodoroDataDTO {
        private String performanceDate; // 수행일자
        private Integer durationMinutes; // 수행시간 (분)
        private Integer dailyCount;      // 일자별 수행횟수
    }
}
