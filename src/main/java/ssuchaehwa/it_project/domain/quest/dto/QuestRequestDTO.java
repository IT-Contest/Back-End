package ssuchaehwa.it_project.domain.quest.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssuchaehwa.it_project.domain.model.enums.CompletionStatus;
import ssuchaehwa.it_project.domain.model.enums.QuestType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class QuestRequestDTO {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestCreateRequest {

        @Column(length = 100)
        private String content;

        private int priority;
        private QuestType questType;
        private CompletionStatus completionStatus;
        private LocalTime startTime;
        private LocalTime endTime;
        private LocalDate startDate;
        private LocalDate dueDate;
        private List<String> hashtags;
    }
}
