package ssuchaehwa.it_project.domain.pomodoro.converter;

import ssuchaehwa.it_project.domain.pomodoro.domain.entity.Pomodoro;
import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroResponseDTO;

public class PomodoroConverter {

    // 뽀모도로 완료 응답 변환
    public static PomodoroResponseDTO.PomodoroCompleteResponse toCompleteResponse(Pomodoro pomodoro) {
        return PomodoroResponseDTO.PomodoroCompleteResponse.builder()
                .earnedExp(pomodoro.getRewardExp())
                .earnedGold(pomodoro.getRewardGold())
                .startTime(pomodoro.getStartTime())
                .endTime(pomodoro.getEndTime())
                .build();
    }
}