package ssuchaehwa.it_project.domain.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveCoachingRequestDTO {
    
    // 분석 타입 (DAILY, WEEKLY, MONTHLY, YEARLY)
    private String analysisType;
    
    // 분석 대상 (QUEST, POMODORO)
    private String questOrPomodoro;
    
    // ChatGPT 응답 전체 (JSON 문자열)
    private String coachingContent;
}
