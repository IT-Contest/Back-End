package ssuchaehwa.it_project.domain.analysis.application;

import ssuchaehwa.it_project.domain.analysis.dto.CoachingRequestDTO;
import ssuchaehwa.it_project.domain.analysis.dto.CoachingResponseDTO;
import ssuchaehwa.it_project.domain.analysis.dto.CoachingRecordDTO;
import ssuchaehwa.it_project.domain.analysis.dto.AnalysisDataDTO;

import java.util.List;

public interface CoachingService {
    
    /**
     * AI 코칭 분석 요청
     * @param userId 사용자 ID
     * @param request 코칭 요청 정보
     * @return AI 코칭 응답
     */
    CoachingResponseDTO requestAICoaching(Long userId, CoachingRequestDTO request);
    
    /**
     * 코칭 기록 저장
     * @param userId 사용자 ID
     * @param coachingContent AI 코칭 내용
     * @param analysisType 분석 타입
     * @param questOrPomodoro 분석 대상
     * @return 저장된 코칭 기록
     */
    CoachingRecordDTO saveCoachingRecord(Long userId, String coachingContent, 
        String analysisType, String questOrPomodoro);
    
    /**
     * 사용자별 코칭 기록 조회 (최신순)
     * @param userId 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 코칭 기록 목록
     */
    List<CoachingRecordDTO> getCoachingRecords(Long userId, int page, int size);
    

    
    /**
     * 분석 데이터 수집
     * @param userId 사용자 ID
     * @param analysisType 분석 타입
     * @param questOrPomodoro 분석 대상
     * @return 수집된 분석 데이터
     */
    AnalysisDataDTO collectAnalysisData(Long userId, String analysisType, String questOrPomodoro);
}
