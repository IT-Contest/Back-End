package ssuchaehwa.it_project.domain.quest.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssuchaehwa.it_project.domain.model.enums.CompletionStatus;
import ssuchaehwa.it_project.domain.model.enums.InvitationStatus;
import ssuchaehwa.it_project.domain.model.enums.QuestType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class QuestRequestDTO {

    // 퀘스트 생성 요청 DTO
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

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FriendInviteRequest {

        private List<Long> invitedFriendIds;
    }


    // 파티 생성 요청 DTO
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PartyCreateRequest {

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
        private List<Long> invitedFriendIds;
    }

    // 퀘스트 완료 / 취소 처리
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestStatusChangeRequest {

        private List<Long> questIds;
        private String completionStatus;
    }

    // 파티 수락 / 거절
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartyInvitationResponseRequest {

        private Long partyId;
        private InvitationStatus responseStatus;
    }
}
