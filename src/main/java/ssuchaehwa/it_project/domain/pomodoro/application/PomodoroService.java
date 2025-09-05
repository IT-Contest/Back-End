package ssuchaehwa.it_project.domain.pomodoro.application;

import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroResponseDTO;

public interface PomodoroService {
    Long startPomodoro(Long userId);
    PomodoroResponseDTO.PomodoroCompleteResponse completePomodoro(Long userId, Long pomodoroId);
    void cancelPomodoro(Long userId, Long pomodoroId);
}