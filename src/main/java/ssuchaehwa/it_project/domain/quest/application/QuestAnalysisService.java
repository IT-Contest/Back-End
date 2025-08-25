package ssuchaehwa.it_project.domain.quest.application;

import ssuchaehwa.it_project.domain.quest.domain.entity.QuestOccurrence;
import ssuchaehwa.it_project.domain.quest.dto.AnalysisResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface QuestAnalysisService {

    // 일일 분석 (D-7 ~ 오늘, 총 7개)
    List<AnalysisResponseDTO.Daily> getDaily(Long userId);

    // 주간 분석 (이번 달 주차별, 총 5개)
    List<AnalysisResponseDTO.Weekly> getWeekly(Long userId);

    // 월간 분석 (이번 달 포함 전월 12개)
    List<AnalysisResponseDTO.Monthly> getMonthly(Long userId);

    // 연간 분석 (이번 년도 포함 전년 10개)
    List<AnalysisResponseDTO.Yearly> getYearly(Long userId);

    // 경계 계산 유틸리티 메서드들
    LocalDate currentPeriodKeyFromAnchor(String questType, LocalDate anchor, LocalDate today);

    void ensureCurrentOccurrences(List<QuestOccurrence> occurrences, Long userId, LocalDate today);
}
