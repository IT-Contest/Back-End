package ssuchaehwa.it_project.domain.pomodoro.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssuchaehwa.it_project.domain.pomodoro.application.PomodoroAnalysisService;
import ssuchaehwa.it_project.domain.pomodoro.converter.PomodoroConverter;
import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroAnalysisResponseDTO;
import ssuchaehwa.it_project.global.common.response.BaseResponse;
import ssuchaehwa.it_project.global.config.security.auth.UserPrincipal;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/pomodoros/analysis")
public class PomodoroAnalysisController {

    private final PomodoroAnalysisService pomodoroAnalysisService;

    @GetMapping("/daily")
    @Operation(summary = "일일 뽀모도로 분석 API", description = "사용자의 일일 뽀모도로 완료 현황을 분석합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK, 일일 분석을 완료했습니다.")
    })
    public BaseResponse<List<PomodoroAnalysisResponseDTO.Daily>> getDailyAnalysis(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Long userId = principal.getId();
        List<PomodoroAnalysisResponseDTO.Daily> rows = pomodoroAnalysisService.getDaily(userId, from, to);
        return PomodoroConverter.toDailyAnalysisResponse(rows);
    }

    @GetMapping("/weekly")
    @Operation(summary = "주간 뽀모도로 분석 API", description = "사용자의 주간 뽀모도로 완료 현황을 분석합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK, 주간 분석을 완료했습니다.")
    })
    public BaseResponse<List<PomodoroAnalysisResponseDTO.Weekly>> getWeeklyAnalysis(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Long userId = principal.getId();
        List<PomodoroAnalysisResponseDTO.Weekly> rows = pomodoroAnalysisService.getWeekly(userId, from, to);
        return PomodoroConverter.toWeeklyAnalysisResponse(rows);
    }

    @GetMapping("/monthly")
    @Operation(summary = "월간 뽀모도로 분석 API", description = "사용자의 월간 뽀모도로 완료 현황을 분석합니다.")
    public BaseResponse<List<PomodoroAnalysisResponseDTO.Monthly>> getMonthlyAnalysis(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Long userId = principal.getId();
        var rows = pomodoroAnalysisService.getMonthly(userId, from, to);
        return PomodoroConverter.toMonthlyAnalysisResponse(rows);
    }

    @GetMapping("/yearly")
    @Operation(summary = "연간 뽀모도로 분석 API", description = "사용자의 연간 뽀모도로 완료 현황을 분석합니다.")
    public BaseResponse<List<PomodoroAnalysisResponseDTO.Yearly>> getYearlyAnalysis(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Long userId = principal.getId();
        var rows = pomodoroAnalysisService.getYearly(userId, from, to);
        return PomodoroConverter.toYearlyAnalysisResponse(rows);
    }
}
