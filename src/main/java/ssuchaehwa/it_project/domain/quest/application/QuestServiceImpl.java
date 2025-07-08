package ssuchaehwa.it_project.domain.quest.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssuchaehwa.it_project.domain.model.enums.CompletionStatus;
import ssuchaehwa.it_project.domain.quest.converter.QuestConverter;
import ssuchaehwa.it_project.domain.quest.domain.entity.Hashtag;
import ssuchaehwa.it_project.domain.quest.domain.entity.HashtagQuest;
import ssuchaehwa.it_project.domain.quest.domain.entity.Quest;
import ssuchaehwa.it_project.domain.quest.domain.repository.HashtagQuestRepository;
import ssuchaehwa.it_project.domain.quest.domain.repository.HashtagRepository;
import ssuchaehwa.it_project.domain.quest.domain.repository.QuestRepository;
import ssuchaehwa.it_project.domain.quest.dto.QuestRequestDTO;
import ssuchaehwa.it_project.domain.quest.dto.QuestResponseDTO;
import ssuchaehwa.it_project.domain.quest.exception.QuestException;
import ssuchaehwa.it_project.domain.user.entity.User;
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
    private final HashtagQuestRepository hashtagQuestRepository;

    @Transactional
    @Override
    public QuestResponseDTO.QuestCreateResponse createQuest(QuestRequestDTO.QuestCreateRequest request) {

        // 일단 1번 유저로 테스트
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new QuestException(ErrorStatus.NO_SUCH_USER));

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
}