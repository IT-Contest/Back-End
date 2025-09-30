package ssuchaehwa.it_project.domain.quest.dto;

import jakarta.persistence.Column;
import lombok.*;
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

        private Long toUserId;
    }

    // 파티 생성
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PartyCreateRequest {

        @Column(length = 100)
        private String partyTitle;
        private String questName;
        private int priority;
        private QuestType questType;
        private CompletionStatus completionStatus;
        private LocalTime startTime;
        private LocalTime endTime;
        private LocalDate startDate;
        private LocalDate dueDate;
        private List<String> hashtags;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartyInviteRequest {
        private List<Long> friendIds;
    }

    // 파티 수정
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PartyUpdateRequest {

        private String partyTitle;
        private String questName;
        private int priority;
        private QuestType questType;
        private CompletionStatus completionStatus;
        private LocalTime startTime;
        private LocalTime endTime;
        private LocalDate startDate;
        private LocalDate dueDate;
        private List<String> hashtags;
    }

    // 퀘스트 완료 / 취소 처리
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestStatusChangeRequest {

        private List<Long> questIds;
        private String completionStatus;
        private Integer expReward;   // 클라이언트에서 보낸 보상값
        private Integer goldReward;  // 클라이언트에서 보낸 보상값
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

    // 퀘스트 수정 요청 DTO
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestUpdateRequest {

        @Column(length = 100)
        private String content;

        private int priority;
        private QuestType questType;
        private List<String> hashtags;

        private LocalTime startTime;
        private LocalTime endTime;
        private LocalDate startDate;
        private LocalDate dueDate;
    }

    // 파티 완료 / 취소 처리
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartyStatusChangeRequest {
        private List<Long> partyIds;          // 상태 변경할 파티 ID 리스트
        private String completionStatus;      // COMPLETED / INCOMPLETE
        private Integer expReward;            // 클라이언트 보낸 보상값 (선택)
        private Integer goldReward;           // 클라이언트 보낸 보상값 (선택)
    }
}
