package ssuchaehwa.it_project.domain.quest.converter;

import ssuchaehwa.it_project.domain.model.enums.CompletionStatus;
import ssuchaehwa.it_project.domain.quest.domain.entity.Hashtag;
import ssuchaehwa.it_project.domain.quest.domain.entity.Party;
import ssuchaehwa.it_project.domain.quest.domain.entity.PartyUser;
import ssuchaehwa.it_project.domain.quest.domain.entity.Quest;
import ssuchaehwa.it_project.domain.quest.dto.QuestResponseDTO;
import ssuchaehwa.it_project.domain.quest.dto.AnalysisResponseDTO;
import ssuchaehwa.it_project.domain.user.domain.entity.User;
import ssuchaehwa.it_project.global.common.response.BaseResponse;
import ssuchaehwa.it_project.global.error.code.status.SuccessStatus;

import java.util.List;

public class QuestConverter {

    // 퀘스트 생성
    public static QuestResponseDTO.QuestCreateResponse toQuestCreateResponse(Quest quest) {
        return QuestResponseDTO.QuestCreateResponse.builder()
                .content(quest.getTitle())
                .questType(quest.getQuestType())
                .startDate(quest.getStartDate())
                .dueDate(quest.getDueDate())
                .startTime(quest.getStartTime())
                .endTime(quest.getEndTime())
                .build();
    }

    // 친구 초대

    // 친구 조회
    public static List<QuestResponseDTO.FriendListResponse> toFriendListResponse(List<User> friends) {
        return friends.stream()
                .map(user -> QuestResponseDTO.FriendListResponse.builder()
                        .userId(user.getId())
                        .nickname(user.getNickname())
                        .profileImageUrl(user.getProfileImageUrl())
                        .level(user.getLevel())
                        .exp(user.getExp())
                        .gold(user.getGold())
                        .build())
                .toList();
    }

    // 퀘스트 조회
    public static List<QuestResponseDTO.QuestListResponse> toQuestListResponse(List<Quest> quests) {
        return quests.stream()
                .map(quest -> QuestResponseDTO.QuestListResponse.builder()
                        .questId(quest.getId())
                        .title(quest.getTitle())
                        .expReward(quest.getExpReward())
                        .goldReward(quest.getGoldReward())
                        .priority(quest.getPriority())
                        .hashtags(
                                quest.getHashtagQuests().stream()
                                        .map(hq -> hq.getHashtag().getName()) // Hashtag → String
                                        .toList()
                        )
                        .partyName(
                                quest.getParty() != null ? quest.getParty().getTitle() : null
                        )
                        .questType(quest.getQuestType())
                        .completionStatus(quest.getCompletionStatus())
                        .startTime(quest.getStartTime())
                        .endTime(quest.getEndTime())
                        .startDate(quest.getStartDate())
                        .dueDate(quest.getDueDate())
                        .build())
                .toList();
    }

    // 퀘스트 목록 조회 (실제 완료 상태 반영)
    public static QuestResponseDTO.QuestListResponse toQuestListResponseWithStatus(Quest quest, CompletionStatus actualStatus) {
        return QuestResponseDTO.QuestListResponse.builder()
                .questId(quest.getId())
                .title(quest.getTitle())
                .expReward(quest.getExpReward())
                .goldReward(quest.getGoldReward())
                .priority(quest.getPriority())
                .hashtags(
                        quest.getHashtagQuests().stream()
                                .map(hq -> hq.getHashtag().getName())
                                .toList()
                )
                .partyName(
                        quest.getParty() != null ? quest.getParty().getTitle() : null
                )
                .questType(quest.getQuestType())
                .completionStatus(actualStatus) // 실제 완료 상태 사용
                .startTime(quest.getStartTime())
                .endTime(quest.getEndTime())
                .startDate(quest.getStartDate())
                .dueDate(quest.getDueDate())
                .build();
    }

    // 메인 페이지 조회
    public static QuestResponseDTO.MainPageResponse toMainPageResponse(
            User user,
            int dailyCount,
            int weeklyCount,
            int monthlyCount,
            int yearlyCount,
            List<QuestResponseDTO.FriendList> friendLists,
            List<QuestResponseDTO.DailyOngoingQuest> dailyOngoingQuests
    ) {
        // 경험치 바 진행률 계산
        double expPercent = calculateExpPercent(user.getExp(), user.getLevel());
        
        return QuestResponseDTO.MainPageResponse.builder()
                .nickname(user.getNickname())
                .level(user.getLevel())
                .exp(user.getExp())
                .expPercent(expPercent)
                .gold(user.getGold())
                .profileImageUrl(user.getProfileImageUrl())
                .dailyCount(dailyCount)
                .weeklyCount(weeklyCount)
                .monthlyCount(monthlyCount)
                .yearlyCount(yearlyCount)
                .friends(friendLists)
                .dailyOngoingQuests(dailyOngoingQuests)
                .build();
    }

    // 경험치 바 진행률 계산 메서드
    private static double calculateExpPercent(int currentExp, int currentLevel) {
        int[] levelThresholds = {
            0, 100, 110, 140, 190, 260, 350, 460, 590, 740, 910,
            1100, 1310, 1540, 1790, 2060, 2350, 2660, 2990, 3340, 3710,
            4100, 4510, 4940, 5390, 5860, 6350, 6860, 7390, 7940, 8510,
            9100, 9710, 10340, 10990, 11660, 12350, 13060, 13790, 14540, 15310,
            16100, 16910, 17740, 18590, 19460, 20350, 21260, 22190, 23140, 24110,
            25100, 26110, 27140, 28190, 29260, 30350, 31460, 32590, 33740, 34910,
            36100, 37310, 38540, 39790, 41060, 42350, 43660, 44990, 46340, 47710,
            49100, 50510, 51940, 53390, 54860, 56350, 57860, 59390, 60940, 62510,
            64100, 65710, 67340, 68990, 70660, 72350, 74060, 75790, 77540, 79310,
            81100, 82910, 84740, 86590, 88460, 90350, 92260, 94190, 96140, 98110
        };
        
        // 최대 레벨(100)인 경우 100% 반환
        if (currentLevel >= levelThresholds.length - 1) {
            return 100.0;
        }
        
        int currentLevelExp = levelThresholds[currentLevel];
        int nextLevelExp = levelThresholds[currentLevel + 1];
        
        // 현재 레벨에서의 진행도 계산
        int expInCurrentLevel = currentExp - currentLevelExp;
        int expNeededForNextLevel = nextLevelExp - currentLevelExp;
        
        // 진행률 계산 (0~100%)
        return Math.min(100.0, Math.max(0.0, (double) expInCurrentLevel / expNeededForNextLevel * 100.0));
    }

    // 퀘스트 완료 상태 변경
    public static List<QuestResponseDTO.QuestStatusChangeResponse> toQuestStatusChangeResponse(
            List<Quest> quests, 
            java.util.Map<Long, Boolean> firstCompletionMap,
            CompletionStatus targetStatus) {
        return quests.stream()
                .map(q -> QuestResponseDTO.QuestStatusChangeResponse.builder()
                        .questId(q.getId())
                        .title(q.getTitle())
                        .completionStatus(targetStatus)
                        .isFirstCompletion(firstCompletionMap.getOrDefault(q.getId(), false))
                        .build())
                .toList();
    }

    // 파티 생성
    public static QuestResponseDTO.PartyCreateResponse toPartyCreateResponse(Party party) {
        return QuestResponseDTO.PartyCreateResponse.builder()
                .questId(party.getQuest().getId())
                .content(party.getTitle())
                .questType(party.getQuestType())
                .startDate(party.getStartDate())
                .dueDate(party.getDueDate())
                .startTime(party.getStartTime())
                .endTime(party.getEndTime())
                .build();
    }

    // 파티 초대
    public static QuestResponseDTO.PartyInviteResponse toPartyInviteResponse(
            Party party,
            User inviter,
            List<User> invitedUsers
    ) {
        return QuestResponseDTO.PartyInviteResponse.builder()
                .partyId(party.getId())
                .inviter(
                        QuestResponseDTO.PartyInviteResponse.Inviter.builder()
                                .userId(inviter.getId())
                                .nickname(inviter.getNickname())
                                .profileImageUrl(inviter.getProfileImageUrl())
                                .build()
                )
                .invitedFriends(
                        invitedUsers.stream()
                                .map(u -> QuestResponseDTO.PartyInviteResponse.InvitedFriend.builder()
                                        .userId(u.getId())
                                        .nickname(u.getNickname())
                                        .profileImageUrl(u.getProfileImageUrl())
                                        .build()
                                ).toList()
                )
                .build();
    }

    // 파티 조회
    public static QuestResponseDTO.PartyListResponse toPartyListResponse(Party party) {
        return QuestResponseDTO.PartyListResponse.builder()
                .partyId(party.getId())
                .title(party.getTitle())
                .questTitle(
                        party.getQuest() != null ? party.getQuest().getTitle() : null
                )
                .status(party.getCompletionStatus())
                .startDate(party.getStartDate())
                .dueDate(party.getDueDate())
                .startTime(party.getStartTime())
                .endTime(party.getEndTime())
                .priority(party.getPriority())
                .questType(party.getQuestType())
                .hashtags(
                        party.getQuest() != null
                                ? party.getQuest().getHashtagQuests().stream()
                                .map(hq -> hq.getHashtag().getName())
                                .toList()
                                : List.of()
                )
                .expiresAt(party.getExpiresAt())
                .build();
    }

    // 파티 초대 리스트
    public static List<QuestResponseDTO.PartyInvitationListResponse> toInvitedPartyListResponse(List<PartyUser> invitations) {
        return invitations.stream().map(pu -> {
            Party party = pu.getParty();
            Quest quest = party.getQuest();
            User host = party.getUser();

            return QuestResponseDTO.PartyInvitationListResponse.builder()
                    .nickname(host.getNickname())
                    .partyName(party.getTitle())
                    .questName(quest.getTitle())
                    .expReward(party.getExpReward())
                    .build();
        }).toList();
    }

    // 파티 수정 응답 변환
    public static QuestResponseDTO.PartyUpdateResponse toPartyUpdateResponse(Party party) {
        return QuestResponseDTO.PartyUpdateResponse.builder()
                .partyId(party.getId())
                .content(party.getTitle())
                .questType(party.getQuestType())
                .completionStatus(party.getCompletionStatus())
                .startTime(party.getStartTime())
                .endTime(party.getEndTime())
                .startDate(party.getStartDate())
                .dueDate(party.getDueDate())
                .message("파티가 성공적으로 수정되었습니다.")
                .build();
    }

    // 파티 삭제 응답 변환
    public static QuestResponseDTO.PartyDeleteResponse toPartyDeleteResponse(Long partyId) {
        return QuestResponseDTO.PartyDeleteResponse.builder()
                .partyId(partyId)
                .deleted(true)
                .message("파티가 성공적으로 삭제되었습니다.")
                .build();
    }

    // 파티 수락 / 거절
    public static QuestResponseDTO.PartyInvitationResponse toPartyInvitationResponse(Party party, PartyUser partyUser) {
        return QuestResponseDTO.PartyInvitationResponse.builder()
                .partyId(party.getId())
                .partyName(party.getTitle())
                .invitationStatus(partyUser.getInvitationStatus())
                .build();
    }

    // 퀘스트 수정 응답 변환
    public static QuestResponseDTO.QuestUpdateResponse toQuestUpdateResponse(Quest quest) {
        return QuestResponseDTO.QuestUpdateResponse.builder()
                .questId(quest.getId())
                .content(quest.getTitle())
                .message("퀘스트가 성공적으로 수정되었습니다.")
                .build();
    }

    // 퀘스트 삭제 응답 변환
    public static QuestResponseDTO.QuestDeleteResponse toQuestDeleteResponse(Long questId) {
        return QuestResponseDTO.QuestDeleteResponse.builder()
                .questId(questId)
                .message("퀘스트가 성공적으로 삭제되었습니다.")
                .build();
    }


    // ========== 퀘스트 분석 응답 변환 메서드들 ==========
    // 분석 응답 변환 - 일일
    public static BaseResponse<List<AnalysisResponseDTO.Daily>> toDailyAnalysisResponse(
            List<AnalysisResponseDTO.Daily> rows) {
        return BaseResponse.onSuccess(SuccessStatus.QUEST_VIEW_SUCCESS, rows);
    }

    // 주간
    public static BaseResponse<List<AnalysisResponseDTO.Weekly>> toWeeklyAnalysisResponse(
            List<AnalysisResponseDTO.Weekly> rows) {
        return BaseResponse.onSuccess(SuccessStatus.QUEST_VIEW_SUCCESS, rows);
    }

    // 월간
    public static BaseResponse<List<AnalysisResponseDTO.Monthly>> toMonthlyAnalysisResponse(
            List<AnalysisResponseDTO.Monthly> rows) {
        return BaseResponse.onSuccess(SuccessStatus.QUEST_VIEW_SUCCESS, rows);
    }

    // 연간
    public static BaseResponse<List<AnalysisResponseDTO.Yearly>> toYearlyAnalysisResponse(
            List<AnalysisResponseDTO.Yearly> rows) {
        return BaseResponse.onSuccess(SuccessStatus.QUEST_VIEW_SUCCESS, rows);
    }
}
