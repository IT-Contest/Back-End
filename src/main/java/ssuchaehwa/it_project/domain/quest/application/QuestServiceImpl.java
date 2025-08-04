package ssuchaehwa.it_project.domain.quest.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssuchaehwa.it_project.domain.model.enums.CompletionStatus;
import ssuchaehwa.it_project.domain.model.enums.InvitationStatus;
import ssuchaehwa.it_project.domain.model.enums.QuestType;
import ssuchaehwa.it_project.domain.quest.converter.QuestConverter;
import ssuchaehwa.it_project.domain.quest.domain.entity.*;
import ssuchaehwa.it_project.domain.quest.domain.repository.*;
import ssuchaehwa.it_project.domain.quest.dto.QuestRequestDTO;
import ssuchaehwa.it_project.domain.quest.dto.QuestResponseDTO;
import ssuchaehwa.it_project.domain.quest.exception.QuestException;
import ssuchaehwa.it_project.domain.user.entity.User;
import ssuchaehwa.it_project.domain.user.exception.UserException;
import ssuchaehwa.it_project.domain.user.repository.UserRepository;
import ssuchaehwa.it_project.global.error.code.status.ErrorStatus;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestServiceImpl implements QuestService {

    private final UserRepository userRepository;
    private final QuestRepository questRepository;
    private final HashtagRepository hashtagRepository;
    private final PartyRepository partyRepository;
    private final PartyUserRepository partyUserRepository;
    private final InvitedFriendRepository invitedFriendRepository;
    private final HashtagQuestRepository hashtagQuestRepository;

    // 퀘스트 생성
    @Transactional
    @Override
    public QuestResponseDTO.QuestCreateResponse createQuest(QuestRequestDTO.QuestCreateRequest request, Long userId) {

        // 일단 1번 유저로 테스트
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.NO_SUCH_USER));

        Quest quest = Quest.builder()
                .user(user)
                .title(request.getContent())
                .priority(request.getPriority())
                .questType(request.getQuestType())
                .completionStatus(request.getCompletionStatus())
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .expReward(3000)
                .goldReward(1250)
                .build();

        questRepository.save(quest);

        List<String> requestHashtag = request.getHashtags();

        // DB에 존재하는 해시태그 조회
        List<Hashtag> existingHashtags = hashtagRepository.findAllByNameIn(requestHashtag);
        Set<String> existingTagNames = existingHashtags.stream()
                .map(Hashtag::getName)
                .collect(Collectors.toSet());

        // 없는 해시태그 추출
        List<Hashtag> newHashtags = requestHashtag.stream()
                .filter(tag -> !existingTagNames.contains(tag))
                .map(tag -> Hashtag.builder().name(tag).build())
                .toList();

        hashtagRepository.saveAll(newHashtags);

        List<Hashtag> allHashtags = new ArrayList<>();
        allHashtags.addAll(existingHashtags);
        allHashtags.addAll(newHashtags);

        List<HashtagQuest> hashtagQuests = allHashtags.stream()
                .map(tag -> HashtagQuest.builder()
                        .hashtag(tag)
                        .quest(quest)
                        .build())
                .toList();
        hashtagQuestRepository.saveAll(hashtagQuests);

        return QuestConverter.toQuestCreateResponse(quest);
    }

    // 파티 생성
    @Transactional
    @Override
    public QuestResponseDTO.PartyCreateResponse createParty(QuestRequestDTO.PartyCreateRequest request, Long questId) {

        User user = userRepository.findById(2L)
                .orElseThrow(() -> new UserException(ErrorStatus.NO_SUCH_USER));

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new QuestException(ErrorStatus.NO_SUCH_QUEST));

        Party party = Party.builder()
                .user(user)
                .quest(quest)
                .title(request.getContent())
                .priority(request.getPriority())
                .questType(request.getQuestType())
                .completionStatus(request.getCompletionStatus())
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        partyRepository.save(party);

        // 친구 초대
        List<Long> invitedIds = request.getInvitedFriendIds();

        if (invitedIds != null && !invitedIds.isEmpty()) {
            List<PartyUser> invitedUsers = invitedIds.stream()
                    .map(userId -> {
                        User invited = userRepository.findById(userId)
                                .orElseThrow(() -> new UserException(ErrorStatus.NO_SUCH_USER));

                        return PartyUser.builder()
                                .party(party)
                                .user(invited)
                                .invitationStatus(InvitationStatus.PENDING)
                                .build();
                    }).toList();

            partyUserRepository.saveAll(invitedUsers);
        }

        List<String> requestHashtag = request.getHashtags();

        // DB에 존재하는 해시태그 조회
        List<Hashtag> existingHashtags = hashtagRepository.findAllByNameIn(requestHashtag);
        Set<String> existingTagNames = existingHashtags.stream()
                .map(Hashtag::getName)
                .collect(Collectors.toSet());

        // 없는 해시태그 추출
        List<Hashtag> newHashtags = requestHashtag.stream()
                .filter(tag -> !existingTagNames.contains(tag))
                .map(tag -> Hashtag.builder().name(tag).build())
                .toList();

        hashtagRepository.saveAll(newHashtags);

        List<Hashtag> allHashtags = new ArrayList<>();
        allHashtags.addAll(existingHashtags);
        allHashtags.addAll(newHashtags);

        List<HashtagQuest> hashtagQuests = allHashtags.stream()
                .map(tag -> HashtagQuest.builder()
                        .hashtag(tag)
                        .quest(quest)
                        .build())
                .toList();
        hashtagQuestRepository.saveAll(hashtagQuests);

        return QuestConverter.toPartyCreateResponse(party);
    }

    // 친구 초대(친구 추가)
//    @Transactional
//    @Override
//    public QuestResponseDTO.FriendInviteResponse friendInvite(QuestRequestDTO.FriendInviteRequest request, Long questId) {
//
//        Quest quest = questRepository.findById(questId)
//                .orElseThrow(() -> new QuestException(ErrorStatus.NO_SUCH_QUEST));
//
//        // 초대한 친구 추가
//        List<Long> invitedFriendIds = request.getInvitedFriendIds();
//
//        List<String> nicknameList = new ArrayList<>();
//
//        if (invitedFriendIds != null && !invitedFriendIds.isEmpty()) {
//            List<User> invitedFriends = userRepository.findAllById(invitedFriendIds);
//
//            List<InvitedFriend> invitedFriendEntities = invitedFriends.stream()
//                    .map(friend -> InvitedFriend.builder()
//                            .quest(quest)
//                            .user(friend)
//                            .build())
//                    .toList();
//
//            // 친구 닉네임
//            nicknameList = invitedFriends.stream()
//                    .map(User::getNickname)
//                    .toList();
//
//            invitedFriendRepository.saveAll(invitedFriendEntities);
//        }
//
//        return QuestConverter.toFriendInviteResponse(nicknameList, questId);
//    }

    // 친구 조회
    @Transactional(readOnly = true)
    @Override
    public List<QuestResponseDTO.FriendListResponse> getFriends(Long userId) {
        List<InvitedFriend> accepted = invitedFriendRepository.findAcceptedFriends(userId);

        // 상대방만 추출 (from이 나면 to가 친구, 반대도 마찬가지)
        List<User> friendUsers = accepted.stream()
                .map(f -> f.getFromUser().getId().equals(userId) ? f.getToUser() : f.getFromUser())
                .toList();

        return QuestConverter.toFriendListResponse(friendUsers);
    }


    // 퀘스트 조회(전체 보기)
    @Transactional(readOnly = true)
    @Override
    public List<QuestResponseDTO.QuestListResponse> getQuests(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.NO_SUCH_USER));

        List<Quest> quests = questRepository.findAllByUserId(user.getId());

        return QuestConverter.toQuestListResponse(quests);
    }

    // 메인 화면 조회
    @Transactional(readOnly = true)
    @Override
    public QuestResponseDTO.MainPageResponse getMainPage(Long userId) {

        // 임시로 1로 테스트
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.NO_SUCH_USER));

        List<Quest> quests = questRepository.findAllByUserId(user.getId());

        // 퀘스트 유형 별 카운트
        int dailyCount = (int) quests.stream()
                .filter(q -> q.getQuestType() == QuestType.DAILY)
                .count();

        int weeklyCount = (int) quests.stream()
                .filter(q -> q.getQuestType() == QuestType.WEEKLY)
                .count();

        int monthlyCount = (int) quests.stream()
                .filter(q -> q.getQuestType() == QuestType.MONTHLY)
                .count();

        int yearlyCount = (int) quests.stream()
                .filter(q -> q.getQuestType() == QuestType.YEARLY)
                .count();

        // 친구 관계에서 ACCEPTED만 추출
        List<InvitedFriend> allFriends = invitedFriendRepository.findAcceptedFriends(userId);

        // 친구 유저 객체 추출 (상대방만)
        List<User> friendUsers = allFriends.stream()
                .map(f -> f.getFromUser().getId().equals(userId) ? f.getToUser() : f.getFromUser())
                .toList();


        // 친구의 필요한 정보만 추출
        List<QuestResponseDTO.FriendList> friendList = friendUsers.stream()
                .map(users -> QuestResponseDTO.FriendList.builder()
                        .userId(users.getId())
                        .nickname(users.getNickname())
                        .exp(users.getExp())
                        .gold(users.getGold())
                        .profileImageUrl(users.getProfileImageUrl())
                        .build())
                .toList();

        // 진행 중인 퀘스트의 필요한 정보만 추출
        List<QuestResponseDTO.DailyOngoingQuest> dailyOngoingQuests = quests.stream()
                .filter(q -> !q.getCompletionStatus().equals(CompletionStatus.COMPLETED))
                .map(q -> QuestResponseDTO.DailyOngoingQuest.builder()
                        .title(q.getTitle())
                        .exp(q.getExpReward())
                        .gold(q.getGoldReward())
                        .partyName(
                                q.getParty() != null ? q.getParty().getTitle() : null
                        )
                        .build())
                .toList();

        // .filter(q -> q.getQuestType() == QuestType.DAILY && !q.getCompletionStatus().equals(CompletionStatus.COMPLETED))
        return QuestConverter.toMainPageResponse(
                user,
                dailyCount,
                weeklyCount,
                monthlyCount,
                yearlyCount,
                friendList,
                dailyOngoingQuests
        );
    }

    // 퀘스트 완료 / 취소
    @Transactional
    @Override
    public List<QuestResponseDTO.QuestStatusChangeResponse> changeQuestStatus(QuestRequestDTO.QuestStatusChangeRequest request, Long userId) {

        // 임시로 1L 사용
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.NO_SUCH_USER));

        List<Long> questIds = request.getQuestIds();

        // 유저 소유의 퀘스트 중에서 해당 ID들에 속하는 것만 필터링
        List<Quest> quests = questRepository.findAllById(questIds).stream()
                .filter(q -> q.getUser().getId().equals(user.getId()))
                .toList();


        // 요청에서 넘겨준 completionStatus(String → Enum)
        CompletionStatus targetStatus = CompletionStatus.valueOf(request.getCompletionStatus().toUpperCase());

        // 상태 적용
        for (Quest quest : quests) {
            setCompletionStatusReflectively(quest, targetStatus);
        }

        return QuestConverter.toQuestStatusChangeResponse(quests);
    }

    // 파티 초대 리스트 조회
    @Transactional(readOnly = true)
    @Override
    public List<QuestResponseDTO.PartyInvitationListResponse> getInvitedPartyList(Long userId) {

        List<PartyUser> invitations = partyUserRepository.findAllByUserIdAndInvitationStatus(userId, InvitationStatus.PENDING);

        return QuestConverter.toInvitedPartyListResponse(invitations);
    }

    // 파티 수락 / 거절
    @Transactional
    @Override
    public QuestResponseDTO.PartyInvitationResponse respondToInvitation(Long userId, QuestRequestDTO.PartyInvitationResponseRequest request) {

        PartyUser partyUser = partyUserRepository.findByUserIdAndPartyId(userId, request.getPartyId())
                .orElseThrow(() -> new QuestException(ErrorStatus.NO_PARTY_INVITATION));

        // 💡 비즈니스 로직을 서비스 내부에서 수행
        setInvitationStatus(partyUser, request.getResponseStatus());

        Party party = partyUser.getParty();

        return QuestConverter.toPartyInvitationResponse(party, partyUser);
    }


    // 완료 상태 변경 메서드
    private void setCompletionStatusReflectively(Quest quest, CompletionStatus newStatus) {
        try {
            Field field = Quest.class.getDeclaredField("completionStatus");
            field.setAccessible(true);
            field.set(quest, newStatus);
        } catch (Exception e) {
            throw new QuestException(ErrorStatus.QUEST_STATUS_UPDATE_FAILED);
        }
    }

    private void setInvitationStatus(PartyUser partyUser, InvitationStatus newStatus) {
        Field field = ReflectionUtils.findField(PartyUser.class, "invitationStatus");
        field.setAccessible(true);
        ReflectionUtils.setField(field, partyUser, newStatus);
    }

    @Override
    @Transactional
    public QuestResponseDTO.QuestUpdateResponse updateQuest(Long questId, QuestRequestDTO.QuestUpdateRequest request, Long userId) {
        // 퀘스트 존재 여부 및 권한 확인
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new QuestException(ErrorStatus.QUEST_NOT_FOUND));

        // 퀘스트 소유자 확인
        if (!quest.getUser().getId().equals(userId)) {
            throw new QuestException(ErrorStatus.QUEST_ACCESS_DENIED);
        }

        // 기존 해시태그 연관관계 삭제
        hashtagQuestRepository.deleteByQuest(quest);

        // 퀘스트 정보 업데이트
        quest.updateQuest(
                request.getContent(),
                request.getPriority(),
                request.getQuestType(),
                request.getStartTime(),
                request.getEndTime(),
                request.getStartDate(),
                request.getDueDate()
        );

        // 새로운 해시태그 처리
        if (request.getHashtags() != null && !request.getHashtags().isEmpty()) {
            for (String hashtagName : request.getHashtags()) {
                // 해시태그 조회 또는 생성
                Hashtag hashtag = hashtagRepository.findByName(hashtagName)
                        .orElseGet(() -> hashtagRepository.save(
                                Hashtag.builder()
                                        .name(hashtagName)
                                        .build()
                        ));

                // 해시태그-퀘스트 연관관계 생성
                HashtagQuest hashtagQuest = HashtagQuest.builder()
                        .hashtag(hashtag)
                        .quest(quest)
                        .build();

                hashtagQuestRepository.save(hashtagQuest);
            }
        }

        // 수정된 퀘스트 저장
        Quest updatedQuest = questRepository.save(quest);

        return QuestConverter.toQuestUpdateResponse(updatedQuest);
    }

    @Override
    @Transactional
    public QuestResponseDTO.QuestDeleteResponse deleteQuest(Long questId, Long userId) {
        // 퀘스트 존재 여부 및 권한 확인
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new QuestException(ErrorStatus.QUEST_NOT_FOUND));

        // 퀘스트 소유자 확인
        if (!quest.getUser().getId().equals(userId)) {
            throw new QuestException(ErrorStatus.QUEST_ACCESS_DENIED);
        }

        // 연관된 해시태그 관계 삭제
        hashtagQuestRepository.deleteByQuest(quest);

        // 퀘스트 삭제
        questRepository.delete(quest);

        return QuestConverter.toQuestDeleteResponse(questId);
    }
}