package ssuchaehwa.it_project.domain.pomodoro.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class PomodoroResponseDTO {

    @Getter
    @Builder
    public static class PomodoroCompleteResponse {
        private int earnedExp;
        private int earnedGold;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }

    // 나중에 필요하면 히스토리용, 통계용 DTO도 추가 가능
}