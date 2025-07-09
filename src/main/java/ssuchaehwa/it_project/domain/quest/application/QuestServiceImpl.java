package ssuchaehwa.it_project.domain.quest.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssuchaehwa.it_project.domain.model.enums.CompletionStatus;
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
                            .friend(friend)
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
                .map(InvitedFriend::getFriend)  // 즉, User 엔티티
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
}