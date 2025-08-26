package ssuchaehwa.it_project.domain.quest.application;

import ssuchaehwa.it_project.domain.quest.domain.entity.QuestOccurrence;
import ssuchaehwa.it_project.domain.quest.dto.AnalysisResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface QuestAnalysisService {

    // 일일 분석
    List<AnalysisResponseDTO.Daily> getDaily(Long userId, LocalDate from, LocalDate to);

    // 주간 분석
    List<AnalysisResponseDTO.Weekly> getWeekly(Long userId, LocalDate from, LocalDate to);

    // 월간 분석
    List<AnalysisResponseDTO.Monthly> getMonthly(Long userId, LocalDate from, LocalDate to);

    // 연간 분석
    List<AnalysisResponseDTO.Yearly> getYearly(Long userId, LocalDate from, LocalDate to);

    // 경계 계산 유틸리티 메서드들
    LocalDate currentPeriodKeyFromAnchor(String questType, LocalDate anchor, LocalDate today);

    void ensureCurrentOccurrences(List<QuestOccurrence> occurrences, Long userId, LocalDate today);
}