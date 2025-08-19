package ssuchaehwa.it_project.domain.quest.api;

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
import ssuchaehwa.it_project.domain.quest.application.QuestAnalysisService;
import ssuchaehwa.it_project.domain.quest.converter.QuestConverter;
import ssuchaehwa.it_project.domain.quest.dto.AnalysisResponseDTO;
import ssuchaehwa.it_project.global.common.response.BaseResponse;
import ssuchaehwa.it_project.global.config.security.auth.UserPrincipal;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/quests/analysis")
public class QuestAnalysisController {

    private final QuestAnalysisService questAnalysisService;

    @GetMapping("/daily")
    @Operation(summary = "일일 퀘스트 분석 API", description = "사용자의 일일 퀘스트 완료 현황을 분석합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK, 일일 분석을 완료했습니다.")
    })
    public BaseResponse<List<AnalysisResponseDTO.Daily>> getDailyAnalysis(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Long userId = principal.getId();
        List<AnalysisResponseDTO.Daily> rows = questAnalysisService.getDaily(userId, from, to);
        return QuestConverter.toDailyAnalysisResponse(rows);
    }

    @GetMapping("/weekly")
    @Operation(summary = "주간 퀘스트 분석 API", description = "사용자의 주간 퀘스트 완료 현황을 분석합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK, 주간 분석을 완료했습니다.")
    })
    public BaseResponse<List<AnalysisResponseDTO.Weekly>> getWeeklyAnalysis(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Long userId = principal.getId();
        List<AnalysisResponseDTO.Weekly> rows = questAnalysisService.getWeekly(userId, from, to);
        return QuestConverter.toWeeklyAnalysisResponse(rows);
    }

    @GetMapping("/monthly")
    @Operation(summary = "월간 퀘스트 분석 API", description = "사용자의 월간 퀘스트 완료 현황을 분석합니다.")
    public BaseResponse<List<AnalysisResponseDTO.Monthly>> getMonthlyAnalysis(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Long userId = principal.getId();
        var rows = questAnalysisService.getMonthly(userId, from, to);
        return QuestConverter.toMonthlyAnalysisResponse(rows);
    }

    @GetMapping("/yearly")
    @Operation(summary = "연간 퀘스트 분석 API", description = "사용자의 연간 퀘스트 완료 현황을 분석합니다.")
    public BaseResponse<List<AnalysisResponseDTO.Yearly>> getYearlyAnalysis(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Long userId = principal.getId();
        var rows = questAnalysisService.getYearly(userId, from, to);
        return QuestConverter.toYearlyAnalysisResponse(rows);
    }
}