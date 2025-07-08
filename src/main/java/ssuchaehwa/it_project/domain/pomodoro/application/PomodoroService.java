package ssuchaehwa.it_project.domain.pomodoro.application;

import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroRequestDTO;
import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroResponseDTO;
import ssuchaehwa.it_project.domain.user.entity.User;

public interface PomodoroService {
    PomodoroResponseDTO.PomodoroCompleteResponse completePomodoro(User user, PomodoroRequestDTO request);
}