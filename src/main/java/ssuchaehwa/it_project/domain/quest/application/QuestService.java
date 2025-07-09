package ssuchaehwa.it_project.domain.quest.application;

import ssuchaehwa.it_project.domain.quest.dto.QuestRequestDTO;
import ssuchaehwa.it_project.domain.quest.dto.QuestResponseDTO;
import ssuchaehwa.it_project.domain.user.entity.User;

import java.util.List;

public interface QuestService {

    // 퀘스트
    QuestResponseDTO.QuestCreateResponse createQuest(QuestRequestDTO.QuestCreateRequest request);

    // 파티
    QuestResponseDTO.PartyCreateResponse createParty(QuestRequestDTO.PartyCreateRequest request, Long questId);

    // 친구 초대
    QuestResponseDTO.FriendInviteResponse friendInvite(QuestRequestDTO.FriendInviteRequest request, Long questId);

    // 친구 조회
    List<QuestResponseDTO.FriendListResponse> getFriends(Long questId);

    // 퀘스트 조회
    List<QuestResponseDTO.QuestListResponse> getQuests();
}
