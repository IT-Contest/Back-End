package ssuchaehwa.it_project.domain.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssuchaehwa.it_project.domain.model.enums.CompletionStatus;
import ssuchaehwa.it_project.domain.model.enums.InvitationStatus;
import ssuchaehwa.it_project.domain.model.enums.QuestType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class QuestResponseDTO {

    // 퀘스트 생성
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestCreateResponse {

        private String content;
        private QuestType questType;
        private LocalTime startTime;
        private LocalTime endTime;
        private LocalDate startDate;
        private LocalDate dueDate;
    }

    // 파티 생성
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PartyCreateResponse {

        private Long questId;
        private String content;
        private QuestType questType;
        private LocalTime startTime;
        private LocalTime endTime;
        private LocalDate startDate;
        private LocalDate dueDate;
    }

    // 파티 수정
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PartyUpdateResponse {
        private Long partyId;
        private String content;
        private QuestType questType;
        private CompletionStatus completionStatus;
        private LocalTime startTime;
        private LocalTime endTime;
        private LocalDate startDate;
        private LocalDate dueDate;
        private String message;
    }

    // 파티 삭제
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PartyDeleteResponse {
        private Long partyId;
        private boolean deleted;
        private String message;
    }

    // 파티 초대
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PartyInviteResponse {
        private Long partyId;
        private Inviter inviter;                // 초대한 사람
        private List<InvitedFriend> invitedFriends;  // 초대된 사람들

        @Builder
        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Inviter {
            private Long userId;
            private String nickname;
            private String profileImageUrl;
        }

        @Builder
        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class InvitedFriend {
            private Long userId;
            private String nickname;
            private String profileImageUrl;
        }
    }


    // 친구 초대
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FriendInviteResponse {

        private String inviteLink;
    }

    // 친구 조회
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FriendListResponse {

        private Long userId;
        private String nickname;
        private int level;
        private int exp;
        private int gold;
        private String profileImageUrl;
    }

    // 퀘스트 조회
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestListResponse {

        private Long questId;
        private String title;
        private int expReward;
        private int goldReward;
        private int priority;
        private String partyName;
        private QuestType questType;
        private List<String> hashtags;
        private CompletionStatus completionStatus;

        private LocalTime startTime;
        private LocalTime endTime;
        private LocalDate startDate;
        private LocalDate dueDate;
    }

    // 메인 화면 조회 DTO
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainPageResponse {

        private String nickname;
        private int exp;
        private double expPercent;
        private int gold;
        private int level;
        private String profileImageUrl;
        private int dailyCount;
        private int weeklyCount;
        private int monthlyCount;
        private int yearlyCount;
        private List<FriendList> friends;
        private List<DailyOngoingQuest> dailyOngoingQuests;
    }

    // 진행중인 퀘스트 정보
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyOngoingQuest {

        private String title;
        private int exp;
        private int gold;
        private String partyName;
    }

    // 친구 정보
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendList {

        private Long userId;
        private String nickname;
        private int exp;
        private int gold;
        private String profileImageUrl;
    }

    // 퀘스트 완료 / 취소 처리
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestStatusChangeResponse {

        private Long questId;
        private String title;
        private CompletionStatus completionStatus;
        @com.fasterxml.jackson.annotation.JsonProperty("isFirstCompletion")
        private boolean isFirstCompletion; // 실제 보상 지급 여부
        
        // 명시적 getter 추가 (JSON 직렬화 보장)
        public boolean getIsFirstCompletion() {
            return isFirstCompletion;
        }
    }

    // 파티 초대 리스트
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartyInvitationListResponse {

        private String nickname;
        private String partyName;
        private String questName;
        private int expReward;
    }

    // 파티 조회
    @Getter
    @Builder
    public static class PartyListResponse {
        private Long partyId;                 // 파티 ID
        private String title;                 // 파티 제목
        private CompletionStatus status;      // 파티 상태 (INCOMPLETE, IN_PROGRESS, COMPLETED)
        private LocalDateTime expiresAt;      // 만료 시간
        private InvitationStatus invitationStatus; // 수락 여부 상태
        private List<MemberInfo> members;     // 파티 멤버 목록
    }

    @Getter
    @Builder
    public static class MemberInfo {
        private Long userId;         // 유저 ID
        private String nickname;     // 닉네임
        private String profileImage; // 프로필 이미지 URL
    }

    // 파티 수락 / 거절
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartyInvitationResponse {

        private Long partyId;
        private String partyName;
        private InvitationStatus invitationStatus;
    }

    // 퀘스트 수정 응답 DTO
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestUpdateResponse {

        private Long questId;
        private String content;
        private String message;
    }

    // 퀘스트 삭제 응답 DTO
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestDeleteResponse {

        private Long questId;
        private String message;
    }
}