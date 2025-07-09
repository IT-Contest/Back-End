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
@RequestMapping("/pomodoro")
@RequiredArgsConstructor
public class PomodoroController {

    private final PomodoroService pomodoroService;

    @PostMapping("/complete")
    @Operation(summary = "뽀모도로 세션 완료 API", description = "사용자가 완료한 뽀모도로 정보를 저장합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "POMODORO_201", description = "뽀모도로가 성공적으로 완료되었습니다.")
    })
    public ResponseEntity<?> completePomodoro(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody PomodoroRequestDTO request
    ) {
        PomodoroResponseDTO.PomodoroCompleteResponse response = pomodoroService.completePomodoro(principal.getId(), request);
        return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus.POMODORO_COMPLETED, response));
    }
}