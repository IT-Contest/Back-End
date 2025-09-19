package ssuchaehwa.it_project.domain.pomodoro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PomodoroRequestDTO {
    
    @Getter
    @NoArgsConstructor
    public static class StartRequest {
        private int durationMinutes; // 25분 고정도 가능
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PomodoroCompleteRequest {
        private int sessionCount;
        private int totalMinutes;
        private LocalDateTime completedAt;
    }
}