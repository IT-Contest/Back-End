package ssuchaehwa.it_project.domain.pomodoro.application;

import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroAnalysisResponseDTO;

import java.util.List;

public interface PomodoroAnalysisService {

    // 일일 분석 (D-7 ~ 오늘, 총 7개)
    List<PomodoroAnalysisResponseDTO.Daily> getDaily(Long userId);

    // 주간 분석 (이번 달 주차별, 총 5개)
    List<PomodoroAnalysisResponseDTO.Weekly> getWeekly(Long userId);

    // 월간 분석 (이번 달 포함 전월 12개)
    List<PomodoroAnalysisResponseDTO.Monthly> getMonthly(Long userId);

    // 연간 분석 (이번 년도 포함 전년 10개)
    List<PomodoroAnalysisResponseDTO.Yearly> getYearly(Long userId);
}
