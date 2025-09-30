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
        private int userExp;        // 현재 총 경험치
        private int userLevel;      // 현재 레벨
        private int rewardExp;      // 받은 보상 경험치
    }

    // 나중에 필요하면 히스토리용, 통계용 DTO도 추가 가능
}