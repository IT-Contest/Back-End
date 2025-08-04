package ssuchaehwa.it_project.domain.quest.application;

import ssuchaehwa.it_project.domain.quest.dto.QuestRequestDTO;
import ssuchaehwa.it_project.domain.quest.dto.QuestResponseDTO;
import ssuchaehwa.it_project.domain.user.entity.User;

import java.util.List;

public interface QuestService {

    // 퀘스트
    QuestResponseDTO.QuestCreateResponse createQuest(QuestRequestDTO.QuestCreateRequest request, Long userId);

    // 파티
    QuestResponseDTO.PartyCreateResponse createParty(QuestRequestDTO.PartyCreateRequest request, Long questId);

    // 친구 초대
//    QuestResponseDTO.FriendInviteResponse friendInvite(QuestRequestDTO.FriendInviteRequest request, Long questId);

    // 친구 조회
    List<QuestResponseDTO.FriendListResponse> getFriends(Long userId);

    // 퀘스트 조회
    List<QuestResponseDTO.QuestListResponse> getQuests(Long userId);

    // 메인 화면 조회
    QuestResponseDTO.MainPageResponse getMainPage(Long userId);

    // 퀘스트 완료 / 취소
    List<QuestResponseDTO.QuestStatusChangeResponse> changeQuestStatus(QuestRequestDTO.QuestStatusChangeRequest request, Long userId);

    // 파티 초대 리스트 조회
    List<QuestResponseDTO.PartyInvitationListResponse> getInvitedPartyList(Long userId);

    // 파티 수락 / 거절
    QuestResponseDTO.PartyInvitationResponse respondToInvitation(Long userId, QuestRequestDTO.PartyInvitationResponseRequest request);

    // 퀘스트 수정
    QuestResponseDTO.QuestUpdateResponse updateQuest(Long questId, QuestRequestDTO.QuestUpdateRequest request, Long userId);

    // 퀘스트 삭제
    QuestResponseDTO.QuestDeleteResponse deleteQuest(Long questId, Long userId);
}
