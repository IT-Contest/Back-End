package ssuchaehwa.it_project.domain.quest.application;

import ssuchaehwa.it_project.domain.quest.domain.entity.Party;
import ssuchaehwa.it_project.domain.quest.dto.QuestRequestDTO;
import ssuchaehwa.it_project.domain.quest.dto.QuestResponseDTO;

import java.util.List;

public interface QuestService {

    // 퀘스트 생성
    QuestResponseDTO.QuestCreateResponse createQuest(QuestRequestDTO.QuestCreateRequest request, Long userId);

    // 친구 초대 링크 발급
    QuestResponseDTO.FriendInviteResponse friendInvite(Long fromUserId);

    // 친구 초대 수락
    void acceptFriendInvite(String token, Long toUserId);

    // 친구 초대 거절
    void rejectFriendInvite(String token, Long toUserId);

    // 친구 조회
    List<QuestResponseDTO.FriendListResponse> getFriends(Long userId);

    // 퀘스트 조회
    List<QuestResponseDTO.QuestListResponse> getQuests(Long userId);

    // 메인 화면 조회
    QuestResponseDTO.MainPageResponse getMainPage(Long userId);

    // 퀘스트 완료 / 취소
    List<QuestResponseDTO.QuestStatusChangeResponse> changeQuestStatus(QuestRequestDTO.QuestStatusChangeRequest request, Long userId);

    // 파티 생성
    QuestResponseDTO.PartyCreateResponse createParty(Long userId, QuestRequestDTO.PartyCreateRequest request);

    // 파티 초대
    QuestResponseDTO.PartyInviteResponse inviteFriends(Long userId, Long partyId, List<Long> invitedIds);

    // 파티 조회
    List<QuestResponseDTO.PartyListResponse> getMyParties(Long userId);

    // 파티 수정
    QuestResponseDTO.PartyUpdateResponse updateParty(Long userId, Long partyId, QuestRequestDTO.PartyUpdateRequest request);

    // 파티 삭제
    QuestResponseDTO.PartyDeleteResponse deleteParty(Long userId, Long partyId);

    // 파티 초대 리스트 조회
    List<QuestResponseDTO.PartyInvitationListResponse> getInvitedPartyList(Long userId);

    // 파티 수락 / 거절
    QuestResponseDTO.PartyInvitationResponse respondToInvitation(Long userId, QuestRequestDTO.PartyInvitationResponseRequest request);

    // 파티 완료 / 취소
    List<QuestResponseDTO.PartyStatusChangeResponse> changePartyStatus(
            QuestRequestDTO.PartyStatusChangeRequest request, Long userId);

    // 퀘스트 수정
    QuestResponseDTO.QuestUpdateResponse updateQuest(Long questId, QuestRequestDTO.QuestUpdateRequest request, Long userId);

    // 퀘스트 삭제
    QuestResponseDTO.QuestDeleteResponse deleteQuest(Long questId, Long userId);
}
