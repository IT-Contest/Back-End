package ssuchaehwa.it_project.domain.analysis.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoachingRequestDTO {
    
    // 분석 타입: DAILY, WEEKLY, MONTHLY, YEARLY
    private String analysisType;
    
    // 분석 대상: QUEST, POMODORO
    private String questOrPomodoro;
}
