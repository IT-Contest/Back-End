package ssuchaehwa.it_project.domain.quest.converter;

import ssuchaehwa.it_project.domain.model.enums.QuestType;
import ssuchaehwa.it_project.domain.quest.domain.entity.Party;
import ssuchaehwa.it_project.domain.quest.domain.entity.PartyUser;
import ssuchaehwa.it_project.domain.quest.domain.entity.Quest;
import ssuchaehwa.it_project.domain.quest.dto.QuestResponseDTO;
import ssuchaehwa.it_project.domain.user.entity.User;

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

    // 친구 초대
    public static QuestResponseDTO.FriendInviteResponse toFriendInviteResponse(List<String> nickNames, Long questId) {
        return QuestResponseDTO.FriendInviteResponse.builder()
                .questId(questId)
                .friendNicknames(nickNames)
                .build();
    }

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
                        .partyName(
                                quest.getParty() != null ? quest.getParty().getTitle() : null
                        )
                        .build())
                .toList();
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
        return QuestResponseDTO.MainPageResponse.builder()
                .nickname(user.getNickname())
                .level(user.getLevel())
                .exp(user.getExp())
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

    // 퀘스트 완료 상태 변경
    public static List<QuestResponseDTO.QuestStatusChangeResponse> toQuestStatusChangeResponse(List<Quest> quests) {
        return quests.stream()
                .map(q -> QuestResponseDTO.QuestStatusChangeResponse.builder()
                        .questId(q.getId())
                        .title(q.getTitle())
                        .completionStatus(q.getCompletionStatus())
                        .build())
                .toList();
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

    // 파티 수락 / 거절
    public static QuestResponseDTO.PartyInvitationResponse toPartyInvitationResponse(Party party, PartyUser partyUser) {
        return QuestResponseDTO.PartyInvitationResponse.builder()
                .partyId(party.getId())
                .partyName(party.getTitle())
                .invitationStatus(partyUser.getInvitationStatus())
                .build();
    }
}
