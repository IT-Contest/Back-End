package ssuchaehwa.it_project.domain.pomodoro.application;

import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroRequestDTO;
import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroResponseDTO;

public interface PomodoroService {
    Long startPomodoro(Long userId);
    PomodoroResponseDTO.PomodoroCompleteResponse completePomodoro(Long userId, Long pomodoroId);
    void cancelPomodoro(Long userId, Long pomodoroId);
    
    // 프론트엔드 호환용 새로운 완료 API
    PomodoroResponseDTO.PomodoroCompleteResponse completeSession(Long userId, PomodoroRequestDTO.PomodoroCompleteRequest request);
}