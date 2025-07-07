package ssuchaehwa.it_project.domain.pomodoro.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PomodoroRequestDTO {
    private int durationMinutes; // 25분 고정도 가능
}