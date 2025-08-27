package ssuchaehwa.it_project.domain.analysis.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoachingRecordDTO {
    
    // 코칭 기록 ID
    private Long id;
    
    // 분석 진행일자 (YYYY.MM.DD 형식)
    private String analysisDate;
    
    // 분석 타입 (일일/주간/월간/연간)
    private String analysisType;
    
    // 분석 대상 (퀘스트/뽀모도로)
    private String questOrPomodoro;
    
    // 코칭 내용 (축약된 버전 - 첫 100자)
    private String coachingContentSummary;
    
    // 전체 코칭 내용
    private String coachingContent;
    
    // 생성일시
    private String createdAt;
}
