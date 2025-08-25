package ssuchaehwa.it_project.domain.pomodoro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PomodoroAnalysisResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Daily {
        private String date;        // "MM-DD" 형식 (예: "08-31")
        private int completed;      // 완료된 세션 수
        private int total;          // 전체 세션 수
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Weekly {
        private String week;        // "MM월 N주차" 형식 (예: "8월 5주차")
        private int completed;      // 완료된 세션 수
        private int total;          // 전체 세션 수
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Monthly {
        private String month;       // "MM월" 형식 (예: "12월")
        private int completed;      // 완료된 세션 수
        private int total;          // 전체 세션 수
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Yearly {
        private String year;        // "YYYY년" 형식 (예: "2025년")
        private int completed;      // 완료된 세션 수
        private int total;          // 전체 세션 수
    }
}
