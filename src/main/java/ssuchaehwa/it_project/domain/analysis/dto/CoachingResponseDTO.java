package ssuchaehwa.it_project.domain.analysis.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoachingResponseDTO {
    
    // AI가 생성한 코칭 내용
    private String coachingContent;
    
    // 분석 타입
    private String analysisType;
    
    // 분석 대상
    private String questOrPomodoro;
    
    // 분석 진행일자 (YYYY.MM.DD 형식)
    private String analysisDate;
    
    // 분석 가능 여부 (일일 제한 확인)
    private boolean canAnalyze;
    
    // 오류 메시지 (분석 불가능한 경우)
    private String errorMessage;
}
