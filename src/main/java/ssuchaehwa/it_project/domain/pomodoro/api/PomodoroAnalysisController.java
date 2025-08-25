package ssuchaehwa.it_project.domain.pomodoro.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssuchaehwa.it_project.domain.pomodoro.application.PomodoroAnalysisService;
import ssuchaehwa.it_project.domain.pomodoro.converter.PomodoroConverter;
import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroAnalysisResponseDTO;
import ssuchaehwa.it_project.global.common.response.BaseResponse;
import ssuchaehwa.it_project.global.config.security.auth.UserPrincipal;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/pomodoros/analysis")
public class PomodoroAnalysisController {

    private final PomodoroAnalysisService pomodoroAnalysisService;

    @GetMapping("/daily")
    @Operation(summary = "일일 뽀모도로 분석 API", description = "사용자의 최근 7일간 일일 뽀모도로 완료 현황을 분석합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK, 일일 분석을 완료했습니다.")
    })
    public BaseResponse<List<PomodoroAnalysisResponseDTO.Daily>> getDailyAnalysis(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal.getId();
        List<PomodoroAnalysisResponseDTO.Daily> rows = pomodoroAnalysisService.getDaily(userId);
        return PomodoroConverter.toDailyAnalysisResponse(rows);
    }

    @GetMapping("/weekly")
    @Operation(summary = "주간 뽀모도로 분석 API", description = "사용자의 이번 달 주차별 뽀모도로 완료 현황을 분석합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK, 주간 분석을 완료했습니다.")
    })
    public BaseResponse<List<PomodoroAnalysisResponseDTO.Weekly>> getWeeklyAnalysis(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal.getId();
        List<PomodoroAnalysisResponseDTO.Weekly> rows = pomodoroAnalysisService.getWeekly(userId);
        return PomodoroConverter.toWeeklyAnalysisResponse(rows);
    }

    @GetMapping("/monthly")
    @Operation(summary = "월간 뽀모도로 분석 API", description = "사용자의 이번 달 포함 전월 12개월 뽀모도로 완료 현황을 분석합니다.")
    public BaseResponse<List<PomodoroAnalysisResponseDTO.Monthly>> getMonthlyAnalysis(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal.getId();
        var rows = pomodoroAnalysisService.getMonthly(userId);
        return PomodoroConverter.toMonthlyAnalysisResponse(rows);
    }

    @GetMapping("/yearly")
    @Operation(summary = "연간 뽀모도로 분석 API", description = "사용자의 이번 년도 포함 전년 10개년 뽀모도로 완료 현황을 분석합니다.")
    public BaseResponse<List<PomodoroAnalysisResponseDTO.Yearly>> getYearlyAnalysis(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal.getId();
        var rows = pomodoroAnalysisService.getYearly(userId);
        return PomodoroConverter.toYearlyAnalysisResponse(rows);
    }
}
