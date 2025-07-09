package ssuchaehwa.it_project.domain.quest.converter;

import ssuchaehwa.it_project.domain.quest.domain.entity.Party;
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
}
