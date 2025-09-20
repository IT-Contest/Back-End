package ssuchaehwa.it_project.domain.pomodoro.converter;

import ssuchaehwa.it_project.domain.pomodoro.domain.entity.Pomodoro;
import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroResponseDTO;
import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroAnalysisResponseDTO;
import ssuchaehwa.it_project.domain.user.domain.entity.User;
import ssuchaehwa.it_project.global.common.response.BaseResponse;
import ssuchaehwa.it_project.global.error.code.status.SuccessStatus;

import java.util.List;

public class PomodoroConverter {

    // 뽀모도로 완료 응답 변환
    public static PomodoroResponseDTO.PomodoroCompleteResponse toCompleteResponse(Pomodoro pomodoro) {
        return PomodoroResponseDTO.PomodoroCompleteResponse.builder()
                .earnedExp(pomodoro.getRewardExp())
                .earnedGold(pomodoro.getRewardGold())
                .startTime(pomodoro.getStartTime())
                .endTime(pomodoro.getEndTime())
                .build();
    }

    // 뽀모도로 완료 응답 변환 (사용자 정보 포함)
    public static PomodoroResponseDTO.PomodoroCompleteResponse toCompleteResponse(Pomodoro pomodoro, User user) {
        return PomodoroResponseDTO.PomodoroCompleteResponse.builder()
                .earnedExp(pomodoro.getRewardExp())
                .earnedGold(pomodoro.getRewardGold())
                .startTime(pomodoro.getStartTime())
                .endTime(pomodoro.getEndTime())
                .userExp(user.getExp())
                .userLevel(user.getLevel())
                .rewardExp(pomodoro.getRewardExp())
                .build();
    }

    // ========== 뽀모도로 분석 응답 변환 메서드들 ==========
    public static BaseResponse<List<PomodoroAnalysisResponseDTO.Daily>> toDailyAnalysisResponse(
            List<PomodoroAnalysisResponseDTO.Daily> rows) {
        return BaseResponse.onSuccess(SuccessStatus.POMODORO_ANALYSIS_SUCCESS, rows);
    }

    public static BaseResponse<List<PomodoroAnalysisResponseDTO.Weekly>> toWeeklyAnalysisResponse(
            List<PomodoroAnalysisResponseDTO.Weekly> rows) {
        return BaseResponse.onSuccess(SuccessStatus.POMODORO_ANALYSIS_SUCCESS, rows);
    }

    public static BaseResponse<List<PomodoroAnalysisResponseDTO.Monthly>> toMonthlyAnalysisResponse(
            List<PomodoroAnalysisResponseDTO.Monthly> rows) {
        return BaseResponse.onSuccess(SuccessStatus.POMODORO_ANALYSIS_SUCCESS, rows);
    }

    public static BaseResponse<List<PomodoroAnalysisResponseDTO.Yearly>> toYearlyAnalysisResponse(
            List<PomodoroAnalysisResponseDTO.Yearly> rows) {
        return BaseResponse.onSuccess(SuccessStatus.POMODORO_ANALYSIS_SUCCESS, rows);
    }
}