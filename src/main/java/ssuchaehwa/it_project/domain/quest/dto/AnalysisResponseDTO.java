package ssuchaehwa.it_project.domain.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class AnalysisResponseDTO {

    @Data
    @AllArgsConstructor
    public static class Daily {
        private String date;
        private int completed;
        private int total;
    }

    @Data
    @AllArgsConstructor
    public static class Weekly {
        private String week;
        private int completed;
        private int total;
    }

    @Data
    @AllArgsConstructor
    public static class Monthly {
        private String month;     // "YYYY-MM"  예: "2025-08"
        private int completed;
        private int total;
    }

    @Data
    @AllArgsConstructor
    public static class Yearly {
        private String year;      // "YYYY"     예: "2025"
        private int completed;
        private int total;
    }
}