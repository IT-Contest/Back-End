package ssuchaehwa.it_project.domain.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
}