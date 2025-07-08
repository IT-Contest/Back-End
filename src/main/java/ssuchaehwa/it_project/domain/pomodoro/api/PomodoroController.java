package ssuchaehwa.it_project.domain.pomodoro.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ssuchaehwa.it_project.domain.pomodoro.application.PomodoroService;
import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroRequestDTO;
import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroResponseDTO;
import ssuchaehwa.it_project.domain.user.entity.User;
import ssuchaehwa.it_project.domain.user.repository.UserRepository;
import ssuchaehwa.it_project.global.config.security.auth.UserPrincipal;

@RestController
@RequestMapping("/pomodoro")
@RequiredArgsConstructor
public class PomodoroController {

    private final PomodoroService pomodoroService;
    private final UserRepository userRepository;

    @PostMapping("/complete")
    public ResponseEntity<?> completePomodoro(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody PomodoroRequestDTO request
    ) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        PomodoroResponseDTO.PomodoroCompleteResponse response = pomodoroService.completePomodoro(user, request);
        return ResponseEntity.ok(response);
    }
}