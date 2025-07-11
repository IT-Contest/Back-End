package ssuchaehwa.it_project.domain.quest.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssuchaehwa.it_project.domain.model.enums.CompletionStatus;
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
    private final InvitedFriendRepository invitedFriendRepository;
    private final HashtagQuestRepository hashtagQuestRepository;

    // 퀘스트 생성
    @Transactional
    @Override
    public QuestResponseDTO.QuestCreateResponse createQuest(QuestRequestDTO.QuestCreateRequest request) {

        // 일단 1번 유저로 테스트
        User user = userRepository.findById(1L)
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

        User user = userRepository.findById(1L)
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
    @Transactional
    @Override
    public QuestResponseDTO.FriendInviteResponse friendInvite(QuestRequestDTO.FriendInviteRequest request, Long questId) {

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new QuestException(ErrorStatus.NO_SUCH_QUEST));

        // 초대한 친구 추가
        List<Long> invitedFriendIds = request.getInvitedFriendIds();

        List<String> nicknameList = new ArrayList<>();

        if (invitedFriendIds != null && !invitedFriendIds.isEmpty()) {
            List<User> invitedFriends = userRepository.findAllById(invitedFriendIds);

            List<InvitedFriend> invitedFriendEntities = invitedFriends.stream()
                    .map(friend -> InvitedFriend.builder()
                            .quest(quest)
                            .user(friend)
                            .build())
                    .toList();

            // 친구 닉네임
            nicknameList = invitedFriends.stream()
                    .map(User::getNickname)
                    .toList();

            invitedFriendRepository.saveAll(invitedFriendEntities);
        }

        return QuestConverter.toFriendInviteResponse(nicknameList, questId);
    }

    // 친구 조회(더보기)
    @Transactional(readOnly = true)
    @Override
    public List<QuestResponseDTO.FriendListResponse> getFriends(Long questId) {

        List<InvitedFriend> invitedFriends = invitedFriendRepository.findAllByQuestId(questId);

        List<User> friends = invitedFriends.stream()
                .map(InvitedFriend::getUser)  // 즉, User 엔티티
                .toList();

        return QuestConverter.toFriendListResponse(friends);
    }

    // 퀘스트 조회(전체 보기)
    @Transactional(readOnly = true)
    @Override
    public List<QuestResponseDTO.QuestListResponse> getQuests() {

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new UserException(ErrorStatus.NO_SUCH_USER));

        List<Quest> quests = questRepository.findAllByUserId(user.getId());

        return QuestConverter.toQuestListResponse(quests);
    }

    // 메인 화면 조회
    @Transactional(readOnly = true)
    @Override
    public QuestResponseDTO.MainPageResponse getMainPage(Long userId) {

        // 임시로 1로 테스트
        User user = userRepository.findById(1L)
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

        // 먼저 매핑 테이블에 등록되어 있는 친구 정보 가져오기
        List<InvitedFriend> allFriends = invitedFriendRepository.findAll();

        // 그 다음 InvitedFriend 테이블에 있는 유저 아이디를 이용하여 친구 리스트를 만듬
        List<User> friendUsers = allFriends.stream()
                .map(InvitedFriend::getUser)
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
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new UserException(ErrorStatus.NO_SUCH_USER));

        List<Long> questIds = request.getQuestIds();

        // 유저 소유의 퀘스트 중에서 해당 ID들에 속하는 것만 필터링
        List<Quest> quests = questRepository.findAllById(questIds).stream()
                .filter(q -> q.getUser().getId().equals(user.getId()))
                .toList();

        // 완료 상태 변경
        for (Quest quest : quests) {
            CompletionStatus current = quest.getCompletionStatus();
            CompletionStatus toggled = (current == CompletionStatus.COMPLETED)
                    ? CompletionStatus.INCOMPLETE
                    : CompletionStatus.COMPLETED;

            setCompletionStatusReflectively(quest, toggled);
        }

        return QuestConverter.toQuestStatusChangeResponse(quests);
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

}