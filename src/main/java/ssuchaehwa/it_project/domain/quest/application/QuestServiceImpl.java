package ssuchaehwa.it_project.domain.quest.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssuchaehwa.it_project.domain.model.enums.CompletionStatus;
import ssuchaehwa.it_project.domain.model.enums.FriendStatus;
import ssuchaehwa.it_project.domain.model.enums.InvitationStatus;
import ssuchaehwa.it_project.domain.model.enums.QuestType;
import ssuchaehwa.it_project.domain.quest.converter.QuestConverter;
import ssuchaehwa.it_project.domain.quest.domain.entity.*;
import ssuchaehwa.it_project.domain.quest.domain.repository.*;
import ssuchaehwa.it_project.domain.quest.domain.repository.QuestOccurrenceRepository;
import ssuchaehwa.it_project.domain.quest.domain.entity.QuestOccurrence;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import ssuchaehwa.it_project.domain.quest.dto.QuestRequestDTO;
import ssuchaehwa.it_project.domain.quest.dto.QuestResponseDTO;
import ssuchaehwa.it_project.domain.quest.exception.QuestException;
import ssuchaehwa.it_project.domain.user.domain.entity.User;
import ssuchaehwa.it_project.domain.user.exception.UserException;
import ssuchaehwa.it_project.domain.user.domain.repository.UserRepository;
import ssuchaehwa.it_project.global.error.code.status.ErrorStatus;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
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
    private final QuestOccurrenceRepository questOccurrenceRepository;
    private final QuestAnalysisService questAnalysisService;

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

        // 현재 기간 occurrence를 즉시 생성 (메인 진입 전에도 DB에서 확인 가능하도록)
        LocalDate _today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate _pk = questAnalysisService.currentPeriodKeyFromAnchor(quest.getQuestType().name(), quest.getStartDate() != null ? quest.getStartDate() : _today, _today);
        boolean _exists = questOccurrenceRepository.existsByTemplateIdAndPeriodKey(quest.getId(), _pk);
        if (!_exists) {
            QuestOccurrence _occ = QuestOccurrence.builder()
                    .templateId(quest.getId())
                    .userId(user.getId())
                    .questType(quest.getQuestType().name())
                    .periodKey(_pk)
                    .status("INCOMPLETE")
                    .expectedStartTime(quest.getStartTime() == null ? null : quest.getStartTime().toString())
                    .expectedEndTime(quest.getEndTime() == null ? null : quest.getEndTime().toString())
                    .title(quest.getTitle())
                    .build();
            questOccurrenceRepository.save(_occ);
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

        return QuestConverter.toQuestCreateResponse(quest);
    }

    // 친구 초대(친구 추가)
    @Transactional
    @Override
    public QuestResponseDTO.FriendInviteResponse friendInvite(Long fromUserId) {

        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new QuestException(ErrorStatus.NO_SUCH_USER));

        // 초대 토큰 생성
        String token = UUID.randomUUID().toString();

        InvitedFriend invitedFriend = InvitedFriend.builder()
                .fromUser(fromUser)
                .token(token)
                .status(FriendStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        invitedFriendRepository.save(invitedFriend);

        // 링크 생성
        String link = "http://192.168.45.148:8080/invite.html?code=" + token;

        return QuestResponseDTO.FriendInviteResponse.builder()
                .inviteLink(link)
                .build();
    }

    // 친구 초대 수락
    @Transactional
    @Override
    public void acceptFriendInvite(String token, Long toUserId) {
        InvitedFriend invite = invitedFriendRepository.findByToken(token)
                .orElseThrow(() -> new QuestException(ErrorStatus.INVALID_INVITE));

        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new UserException(ErrorStatus.NO_SUCH_USER));

        // 이미 수락된 관계가 있는지 체크
        boolean alreadyAccepted = invitedFriendRepository
                .existsByFromUserAndToUserAndStatus(invite.getFromUser(), toUser, FriendStatus.ACCEPTED);

        if (alreadyAccepted) {
            throw new QuestException(ErrorStatus.INVALID_INVITE_STATUS);
        }

        // 새로운 ACCEPTED 레코드 추가 (단톡방 공유 가능, 재수락 가능)
        InvitedFriend accepted = InvitedFriend.builder()
                .fromUser(invite.getFromUser())
                .toUser(toUser)
                .status(FriendStatus.ACCEPTED)
                .token(invite.getToken())
                .expiresAt(LocalDateTime.now().plusYears(100))
                .build();

        invitedFriendRepository.save(accepted);
    }

    // 친구 초대 거절
    @Transactional
    @Override
    public void rejectFriendInvite(String token, Long toUserId) {
        InvitedFriend invite = invitedFriendRepository.findByToken(token)
                .orElseThrow(() -> new QuestException(ErrorStatus.INVALID_INVITE));

        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new UserException(ErrorStatus.NO_SUCH_USER));

        // 이미 응답한 기록이 있는지 체크
        boolean alreadyResponded = invitedFriendRepository
                .existsByFromUserAndToUser(invite.getFromUser(), toUser);

        if (!alreadyResponded) {
            InvitedFriend rejected = InvitedFriend.builder()
                    .fromUser(invite.getFromUser())
                    .toUser(toUser)
                    .token(invite.getToken())
                    .status(FriendStatus.REJECTED)
                    .expiresAt(LocalDateTime.now().plusDays(1))
                    .build();
            invitedFriendRepository.save(rejected);
        }
    }

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
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        // 각 퀘스트의 현재 기간 상태를 QuestOccurrence에서 조회하여 반영
        return quests.stream().map(quest -> {
            // 현재 기간 period_key 계산
            LocalDate periodKey = questAnalysisService.currentPeriodKeyFromAnchor(
                quest.getQuestType().name(), 
                quest.getStartDate() != null ? quest.getStartDate() : today, 
                today
            );
            
            // QuestOccurrence에서 실제 상태 조회
            Optional<QuestOccurrence> occurrence = questOccurrenceRepository
                .findByTemplateIdAndPeriodKey(quest.getId(), periodKey);
            
            // 실제 완료 상태 결정
            CompletionStatus actualStatus = CompletionStatus.INCOMPLETE;
            if (occurrence.isPresent() && "COMPLETED".equalsIgnoreCase(occurrence.get().getStatus())) {
                actualStatus = CompletionStatus.COMPLETED;
            }
            
            return QuestConverter.toQuestListResponseWithStatus(quest, actualStatus);
        }).collect(java.util.stream.Collectors.toList());
    }

    // 메인 화면 조회
    @Transactional
    @Override
    public QuestResponseDTO.MainPageResponse getMainPage(Long userId) {

        // 임시로 1로 테스트
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.NO_SUCH_USER));

        List<Quest> quests = questRepository.findAllByUserId(user.getId());

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        // QuestAnalysisService를 통해 occurrence 생성
        quests.forEach(q -> {
            LocalDate pk = questAnalysisService.currentPeriodKeyFromAnchor(q.getQuestType().name(), q.getStartDate() != null ? q.getStartDate() : today, today);
            boolean exists = questOccurrenceRepository.existsByTemplateIdAndPeriodKey(q.getId(), pk);
            if (!exists) {
                QuestOccurrence occ = QuestOccurrence.builder()
                        .templateId(q.getId())
                        .userId(user.getId())
                        .questType(q.getQuestType().name())
                        .periodKey(pk)
                        .status("INCOMPLETE")
                        .expectedStartTime(q.getStartTime() == null ? null : q.getStartTime().toString())
                        .expectedEndTime(q.getEndTime() == null ? null : q.getEndTime().toString())
                        .title(q.getTitle())
                        .build();
                questOccurrenceRepository.save(occ);
            }
        });

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
        List<QuestResponseDTO.DailyOngoingQuest> dailyOngoingQuests = quests.stream()
                .filter(q -> {
                    LocalDate pk = questAnalysisService.currentPeriodKeyFromAnchor(q.getQuestType().name(), q.getStartDate() != null ? q.getStartDate() : today, today);
                    Optional<QuestOccurrence> occurrence = questOccurrenceRepository.findByTemplateIdAndPeriodKey(q.getId(), pk);
                    // 존재하고 완료되지 않은 퀘스트만 필터링
                    return occurrence.isPresent() && !"COMPLETED".equalsIgnoreCase(occurrence.get().getStatus());
                })
                .map(q -> QuestResponseDTO.DailyOngoingQuest.builder()
                        .title(q.getTitle())
                        .exp(q.getExpReward())
                        .gold(q.getGoldReward())
                        .partyName(q.getParty() != null ? q.getParty().getTitle() : null)
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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.NO_SUCH_USER));

        List<Long> questIds = request.getQuestIds();

        // 유저 소유의 퀘스트만 대상으로 선정
        List<Quest> quests = questRepository.findAllById(questIds).stream()
                .filter(q -> q.getUser().getId().equals(user.getId()))
                .toList();

        // 요청 상태 파싱 (문자열 → Enum)
        CompletionStatus targetStatus = CompletionStatus.valueOf(request.getCompletionStatus().toUpperCase());

        // 오늘 기준 period_key 계산용
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        // 실제 보상 지급 여부를 추적하는 맵
        java.util.Map<Long, Boolean> firstCompletionMap = new java.util.HashMap<>();

        for (Quest quest : quests) {
            // 템플릿의 주기(일/주/월/연)에 맞는 period_key 계산 (앵커 기반)
            LocalDate periodKey = questAnalysisService.currentPeriodKeyFromAnchor(
                quest.getQuestType().name(), 
                quest.getStartDate() != null ? quest.getStartDate() : today, 
                today
            );

            // (template_id, period_key)로 occurrence 조회(or 생성)
            Optional<QuestOccurrence> existing = questOccurrenceRepository.findByTemplateIdAndPeriodKey(quest.getId(), periodKey);
            
            boolean wasIncomplete = true; // 기본값: 새로 생성되거나 INCOMPLETE 상태
            boolean hasEverBeenCompleted = false; // 해당 날짜에 이미 한 번이라도 완료된 적이 있는지
            
            if (existing.isPresent()) {
                // 기존 occurrence가 있으면 실제 상태 및 완료 이력 확인
                QuestOccurrence occ = existing.get();
                String currentStatus = occ.getStatus();
                wasIncomplete = "INCOMPLETE".equalsIgnoreCase(currentStatus);
                hasEverBeenCompleted = (occ.getCompletedAt() != null); // completed_at이 있으면 한 번이라도 완료된 것
            } else {
                // 새로운 occurrence 생성 및 즉시 저장
                QuestOccurrence newOcc = QuestOccurrence.builder()
                        .templateId(quest.getId())
                        .userId(user.getId())
                        .questType(quest.getQuestType().name())
                        .periodKey(periodKey)
                        .status("INCOMPLETE")
                        .expectedStartTime(quest.getStartTime() == null ? null : quest.getStartTime().toString())
                        .expectedEndTime(quest.getEndTime() == null ? null : quest.getEndTime().toString())
                        .title(quest.getTitle())
                        .build();
                questOccurrenceRepository.save(newOcc);
                wasIncomplete = true; // 새로 생성된 것은 항상 INCOMPLETE
                hasEverBeenCompleted = false; // 새로 생성된 것은 완료 이력 없음
            }
            boolean isFirstCompletion = false;

            // 상태 업데이트
            if (targetStatus == CompletionStatus.COMPLETED) {
                // 해당 날짜에 처음으로 완료될 때만 보상 지급
                if (!hasEverBeenCompleted) {
                    isFirstCompletion = true;
                    // 사용자에게 보상 지급
                    user.addExp(quest.getExpReward());
                    user.addGold(quest.getGoldReward());
                    userRepository.save(user);
                }
                
                questOccurrenceRepository.updateStatusByTemplateIdAndPeriodKey(
                    quest.getId(), periodKey, "COMPLETED", LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                );
            } else {
                // INCOMPLETE로 변경 (completed_at은 유지)
                questOccurrenceRepository.updateStatusOnlyByTemplateIdAndPeriodKey(
                    quest.getId(), periodKey, "INCOMPLETE"
                );
            }
            
            firstCompletionMap.put(quest.getId(), isFirstCompletion);
        }

        return QuestConverter.toQuestStatusChangeResponse(quests, firstCompletionMap, targetStatus);
    }

    // 파티 생성
    @Transactional
    @Override
    public QuestResponseDTO.PartyCreateResponse createParty(Long userId,QuestRequestDTO.PartyCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.NO_SUCH_USER));

        Quest quest = questRepository.findByUserIdAndTitle(userId, request.getQuestTitle())
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
                .expiresAt(LocalDateTime.now().plusMinutes(15))
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

    // 파티 초대
    @Transactional
    @Override
    public QuestResponseDTO.PartyInviteResponse inviteFriends(Long userId, Long partyId, List<Long> invitedIds) {

        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new QuestException(ErrorStatus.NO_SUCH_PARTY));

        User inviter = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.NO_SUCH_USER));

        if (invitedIds == null || invitedIds.isEmpty()) {
            return QuestConverter.toPartyInviteResponse(party, inviter, Collections.emptyList());
        }

        List<User> invitedUsers = invitedIds.stream()
                .map(invitedId -> userRepository.findById(invitedId)
                        .orElseThrow(() -> new UserException(ErrorStatus.NO_SUCH_USER)))
                .toList();

        List<PartyUser> partyUsers = invitedUsers.stream()
                .map(user -> PartyUser.builder()
                        .party(party)
                        .user(user)
                        .invitationStatus(InvitationStatus.PENDING)
                        .expiresAt(LocalDateTime.now().plusMinutes(10))
                        .build())
                .toList();

        partyUserRepository.saveAll(partyUsers);

        return QuestConverter.toPartyInviteResponse(party, inviter, invitedUsers);
    }

    // 파티 조회
    @Transactional(readOnly = true)
    @Override
    public List<QuestResponseDTO.PartyListResponse> getMyParties(Long userId) {

        // 내가 만든 파티
        List<Party> created = partyRepository.findAllByUserId(userId);

        // 내가 초대받아 속한 파티
        List<Party> joined = partyRepository.findAllByMemberUserId(userId);

        // 합치고 중복 제거
        List<Party> all = new ArrayList<>();
        all.addAll(created);
        all.addAll(joined);

        List<Party> distinct = all.stream()
                .distinct()
                .toList();

        return distinct.stream()
                .map(QuestConverter::toPartyListResponse) // DTO 변환
                .toList();
    }


    // 파티 수정
    @Transactional
    @Override
    public QuestResponseDTO.PartyUpdateResponse updateParty(Long userId, Long partyId, QuestRequestDTO.PartyUpdateRequest request) {

        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new QuestException(ErrorStatus.NO_SUCH_PARTY));

        if (!party.getUser().getId().equals(userId)) {
            throw new QuestException(ErrorStatus.PARTY_ACCESS_DENIED);
        }

        try {
            party.update(
                    request.getContent(),
                    request.getPriority(),
                    request.getQuestType(),
                    request.getCompletionStatus(),
                    request.getStartDate(),
                    request.getDueDate(),
                    request.getStartTime(),
                    request.getEndTime()
            );

            // 해시태그 처리 로직 재사용
            List<String> requestHashtag = request.getHashtags();
            if (requestHashtag != null) {
                List<Hashtag> existingHashtags = hashtagRepository.findAllByNameIn(requestHashtag);
                Set<String> existingTagNames = existingHashtags.stream()
                        .map(Hashtag::getName)
                        .collect(Collectors.toSet());

                List<Hashtag> newHashtags = requestHashtag.stream()
                        .filter(tag -> !existingTagNames.contains(tag))
                        .map(tag -> Hashtag.builder().name(tag).build())
                        .toList();

                hashtagRepository.saveAll(newHashtags);

                List<Hashtag> allHashtags = new ArrayList<>();
                allHashtags.addAll(existingHashtags);
                allHashtags.addAll(newHashtags);

                // 기존 해시태그 관계 삭제 후 다시 저장
                hashtagQuestRepository.deleteByQuest(party.getQuest());

                List<HashtagQuest> hashtagQuests = allHashtags.stream()
                        .map(tag -> HashtagQuest.builder()
                                .hashtag(tag)
                                .quest(party.getQuest())
                                .build())
                        .toList();

                hashtagQuestRepository.saveAll(hashtagQuests);
            }

            return QuestConverter.toPartyUpdateResponse(party);
        } catch (Exception e) {
            throw new QuestException(ErrorStatus.PARTY_UPDATE_FAILED);
        }
    }

    // 파티 삭제
    @Transactional
    @Override
    public QuestResponseDTO.PartyDeleteResponse deleteParty(Long userId, Long partyId) {

        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new QuestException(ErrorStatus.NO_SUCH_PARTY));

        if (!party.getUser().getId().equals(userId)) {
            throw new QuestException(ErrorStatus.PARTY_ACCESS_DENIED);
        }

        try {
            partyUserRepository.deleteByParty(party);
            partyRepository.delete(party);
            return QuestConverter.toPartyDeleteResponse(partyId);
        } catch (Exception e) {
            throw new QuestException(ErrorStatus.PARTY_DELETE_FAILED);
        }
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
    public QuestResponseDTO.PartyInvitationResponse respondToInvitation(
            Long userId, QuestRequestDTO.PartyInvitationResponseRequest request) {

        PartyUser partyUser = partyUserRepository.findByUserIdAndPartyId(userId, request.getPartyId())
                .orElseThrow(() -> new QuestException(ErrorStatus.NO_PARTY_INVITATION));

        // 초대 상태 변경
        setInvitationStatus(partyUser, request.getResponseStatus());
        partyUserRepository.save(partyUser);

        Party party = partyUser.getParty();
        List<PartyUser> partyUsers = party.getPartyUsers();

        boolean allAccepted = partyUsers.stream().allMatch(pu -> pu.getInvitationStatus() == InvitationStatus.ACCEPTED);
        boolean anyAccepted = partyUsers.stream().anyMatch(pu -> pu.getInvitationStatus() == InvitationStatus.ACCEPTED);
        boolean allResponded = partyUsers.stream().allMatch(pu ->
                pu.getInvitationStatus() == InvitationStatus.ACCEPTED ||
                        pu.getInvitationStatus() == InvitationStatus.DECLINED);

        // 모두 수락 → 바로 IN_PROGRESS
        if (allAccepted) {
            party.changeCompletionStatus(CompletionStatus.IN_PROGRESS);
            partyRepository.save(party);
        }
        // 1명 이상 수락 + 나머지 전부 거절 → 바로 IN_PROGRESS
        else if (anyAccepted && allResponded) {
            party.changeCompletionStatus(CompletionStatus.IN_PROGRESS);
            partyRepository.save(party);
        }
        // 모두 거절 → 삭제 (만료 대기 없이 즉시)
        else if (!anyAccepted && allResponded) {
            partyUserRepository.deleteByParty(party);
            partyRepository.delete(party);
        }
        // 일부 수락 + 일부 대기 → 아직 대기 (만료시간까지 기다림)
        //    ⇒ 따로 상태 변경/삭제 없음

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

    @Transactional
    @Override
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

        // 현재 기간 occurrence가 있으면 제목/예정시간만 스냅샷 업데이트 (과거 이력은 보존)
        LocalDate _today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate _pk = questAnalysisService.currentPeriodKeyFromAnchor(updatedQuest.getQuestType().name(), updatedQuest.getStartDate() != null ? updatedQuest.getStartDate() : _today, _today);
        questOccurrenceRepository.findByTemplateIdAndPeriodKey(updatedQuest.getId(), _pk)
                .ifPresent(occ -> {
                    if (!"COMPLETED".equalsIgnoreCase(occ.getStatus())) {
                        occ.setTitle(updatedQuest.getTitle());
                        occ.setExpectedStartTime(updatedQuest.getStartTime() == null ? null : updatedQuest.getStartTime().toString());
                        occ.setExpectedEndTime(updatedQuest.getEndTime() == null ? null : updatedQuest.getEndTime().toString());
                        questOccurrenceRepository.save(occ);
                    }
                });

        return QuestConverter.toQuestUpdateResponse(updatedQuest);
    }

    @Transactional
    @Override
    public QuestResponseDTO.QuestDeleteResponse deleteQuest(Long questId, Long userId) {
        // 퀘스트 존재 여부 및 권한 확인
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new QuestException(ErrorStatus.QUEST_NOT_FOUND));

        // 퀘스트 소유자 확인
        if (!quest.getUser().getId().equals(userId)) {
            throw new QuestException(ErrorStatus.QUEST_ACCESS_DENIED);
        }

        // 완료된 QuestOccurrence 조회하여 경험치/골드 회수
        List<QuestOccurrence> completedOccurrences = questOccurrenceRepository.findAllByTemplateId(quest.getId())
                .stream()
                .filter(occ -> "COMPLETED".equalsIgnoreCase(occ.getStatus()))
                .toList();
        
        // 완료된 횟수만큼 경험치/골드 차감
        if (!completedOccurrences.isEmpty()) {
            User user = quest.getUser();
            int completedCount = completedOccurrences.size();
            int expToDeduct = quest.getExpReward() * completedCount;
            int goldToDeduct = quest.getGoldReward() * completedCount;
            
            // 사용자의 현재 경험치/골드에서 차감 (0 미만으로는 가지 않도록)
            user.deductExp(expToDeduct);
            user.deductGold(goldToDeduct);
            
            userRepository.save(user);
        }

        // 연관된 해시태그 관계 삭제
        hashtagQuestRepository.deleteByQuest(quest);

        // 템플릿 삭제 전에 모든 occurrence 삭제 (이력 포함)
        questOccurrenceRepository.deleteAllByTemplateId(quest.getId());

        // 퀘스트 삭제
        questRepository.delete(quest);

        return QuestConverter.toQuestDeleteResponse(questId);
    }
}