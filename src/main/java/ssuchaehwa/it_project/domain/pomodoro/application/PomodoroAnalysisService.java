package ssuchaehwa.it_project.domain.pomodoro.application;

import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroAnalysisResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface PomodoroAnalysisService {

    // 일일 분석
    List<PomodoroAnalysisResponseDTO.Daily> getDaily(Long userId, LocalDate from, LocalDate to);

    // 주간 분석
    List<PomodoroAnalysisResponseDTO.Weekly> getWeekly(Long userId, LocalDate from, LocalDate to);

    // 월간 분석
    List<PomodoroAnalysisResponseDTO.Monthly> getMonthly(Long userId, LocalDate from, LocalDate to);

    // 연간 분석
    List<PomodoroAnalysisResponseDTO.Yearly> getYearly(Long userId, LocalDate from, LocalDate to);
}
