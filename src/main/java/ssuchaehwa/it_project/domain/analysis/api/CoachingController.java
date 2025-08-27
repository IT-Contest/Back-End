package ssuchaehwa.it_project.domain.analysis.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ssuchaehwa.it_project.domain.analysis.application.CoachingService;
import ssuchaehwa.it_project.domain.analysis.converter.CoachingConverter;
import ssuchaehwa.it_project.domain.analysis.dto.CoachingRequestDTO;
import ssuchaehwa.it_project.domain.analysis.dto.CoachingResponseDTO;
import ssuchaehwa.it_project.domain.analysis.dto.CoachingRecordDTO;
import ssuchaehwa.it_project.domain.analysis.dto.SaveCoachingRequestDTO;
import ssuchaehwa.it_project.global.common.response.BaseResponse;
import ssuchaehwa.it_project.global.config.security.auth.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/coaching")
@RequiredArgsConstructor
@Validated
public class CoachingController {

    private final CoachingService coachingService;

    @PostMapping("/analyze")
    @Operation(summary = "AI 코칭 분석 요청 API", description = "ChatGPT API를 활용하여 퀘스트/뽀모도로를 분석하고 AI 코칭을 제공합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK, AI 코칭 분석을 완료했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "BAD_REQUEST, 일일 분석 제한에 도달했습니다.")
    })
    public BaseResponse<CoachingResponseDTO> requestAICoaching(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CoachingRequestDTO request) {
        
        Long userId = principal.getId();
        CoachingResponseDTO response = coachingService.requestAICoaching(userId, request);
        
        if (response.isCanAnalyze()) {
            return BaseResponse.onSuccess(ssuchaehwa.it_project.global.error.code.status.SuccessStatus.OK, response);
        } else {
            return BaseResponse.onSuccess(ssuchaehwa.it_project.global.error.code.status.SuccessStatus.OK, response);
        }
    }

    @PostMapping("/save")
    @Operation(summary = "코칭 기록 저장 API", description = "AI 코칭 내용을 데이터베이스에 저장합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK, 코칭 기록을 저장했습니다.")
    })
    public BaseResponse<CoachingRecordDTO> saveCoachingRecord(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody SaveCoachingRequestDTO request) {
        
        Long userId = principal.getId();
        CoachingRecordDTO savedRecord = coachingService.saveCoachingRecord(
                userId, request.getCoachingContent(), request.getAnalysisType(), request.getQuestOrPomodoro());
        
        return BaseResponse.onSuccess(ssuchaehwa.it_project.global.error.code.status.SuccessStatus.OK, savedRecord);
    }

    @GetMapping("/records")
    @Operation(summary = "코칭 기록 조회 API", description = "사용자의 코칭 기록을 최신순으로 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK, 코칭 기록 조회를 완료했습니다.")
    })
    public BaseResponse<List<CoachingRecordDTO>> getCoachingRecords(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Long userId = principal.getId();
        List<CoachingRecordDTO> records = coachingService.getCoachingRecords(userId, page, size);
        
        return CoachingConverter.toCoachingRecordListResponse(records);
    }


}
