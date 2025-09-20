package ssuchaehwa.it_project.domain.pomodoro.api;
import ssuchaehwa.it_project.global.common.response.BaseResponse;
import ssuchaehwa.it_project.global.error.code.status.SuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ssuchaehwa.it_project.domain.pomodoro.application.PomodoroService;
import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroRequestDTO;
import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroResponseDTO;
import ssuchaehwa.it_project.global.config.security.auth.UserPrincipal;

@RestController
@RequiredArgsConstructor
public class PomodoroController {

    private final PomodoroService pomodoroService;

    @PostMapping("/pomodoros/start")
    @Operation(summary = "뽀모도로 세션 시작 API", description = "새로운 뽀모도로 세션을 시작합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "POMODORO_201", description = "뽀모도로가 성공적으로 시작되었습니다.")
    })
    public ResponseEntity<BaseResponse<Long>> startPomodoro(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long pomodoroId = pomodoroService.startPomodoro(principal.getId());
        return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus.POMODORO_STARTED, pomodoroId));
    }

    @PostMapping("/pomodoros/{id}/complete")
    @Operation(summary = "뽀모도로 세션 완료 API", description = "진행중인 뽀모도로를 완료합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "POMODORO_201", description = "뽀모도로가 성공적으로 완료되었습니다.")
    })
    public ResponseEntity<BaseResponse<PomodoroResponseDTO.PomodoroCompleteResponse>> completePomodoro(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long pomodoroId
    ) {
        PomodoroResponseDTO.PomodoroCompleteResponse response = pomodoroService.completePomodoro(principal.getId(), pomodoroId);
        return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus.POMODORO_COMPLETED, response));
    }

    @PostMapping("/pomodoros/{id}/cancel")
    @Operation(summary = "뽀모도로 세션 취소 API", description = "진행중인 뽀모도로를 취소합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "POMODORO_200", description = "뽀모도로가 성공적으로 취소되었습니다.")
    })
    public ResponseEntity<BaseResponse<Void>> cancelPomodoro(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long pomodoroId
    ) {
        pomodoroService.cancelPomodoro(principal.getId(), pomodoroId);
        return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus.POMODORO_CANCELED, null));
    }

    @PostMapping("/pomodoro/complete")
    @Operation(summary = "뽀모도로 세션 완료 API (프론트엔드 호환)", description = "완료된 뽀모도로 세션 정보를 받아 저장합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "POMODORO_201", description = "뽀모도로가 성공적으로 완료되었습니다.")
    })
    public ResponseEntity<BaseResponse<PomodoroResponseDTO.PomodoroCompleteResponse>> completeSession(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody PomodoroRequestDTO.PomodoroCompleteRequest request
    ) {
        PomodoroResponseDTO.PomodoroCompleteResponse response = pomodoroService.completeSession(principal.getId(), request);
        return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus.POMODORO_COMPLETED, response));
    }
}