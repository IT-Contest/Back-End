package ssuchaehwa.it_project.domain.quest.dto;

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


    // 친구 초대
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FriendInviteResponse {

        private Long questId;
        private List<String> friendNicknames;
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
    }

    // 메인 화면 조회 DTO
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainPageResponse {

        private String nickname;
        private int exp;
        private int gold;
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
}