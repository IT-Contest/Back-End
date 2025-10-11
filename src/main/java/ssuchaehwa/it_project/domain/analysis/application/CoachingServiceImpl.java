package ssuchaehwa.it_project.domain.analysis.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssuchaehwa.it_project.domain.analysis.domain.entity.CoachingRecord;
import ssuchaehwa.it_project.domain.analysis.domain.repository.CoachingRecordRepository;
import ssuchaehwa.it_project.domain.analysis.dto.*;
import ssuchaehwa.it_project.domain.analysis.exception.CoachingException;
import ssuchaehwa.it_project.domain.analysis.converter.CoachingConverter;
import ssuchaehwa.it_project.domain.quest.domain.repository.QuestRepository;
import ssuchaehwa.it_project.domain.pomodoro.domain.repository.PomodoroRepository;
import ssuchaehwa.it_project.domain.user.domain.entity.User;
import ssuchaehwa.it_project.domain.user.domain.repository.UserRepository;
import ssuchaehwa.it_project.global.error.code.status.ErrorStatus;
import ssuchaehwa.it_project.domain.analysis.dto.CoachingRecordDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoachingServiceImpl implements CoachingService {

    private final CoachingRecordRepository coachingRecordRepository;
    private final ChatGPTService chatGPTService;
    private final UserRepository userRepository;
    private final QuestRepository questRepository;
    private final ssuchaehwa.it_project.domain.quest.domain.repository.QuestOccurrenceRepository questOccurrenceRepository;
    private final PomodoroRepository pomodoroRepository;

    @Override
    public CoachingResponseDTO requestAICoaching(Long userId, CoachingRequestDTO request) {
        // 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CoachingException(ErrorStatus.NO_SUCH_USER));



        // 분석 데이터 수집
        AnalysisDataDTO analysisData = collectAnalysisData(userId, request.getAnalysisType(), request.getQuestOrPomodoro());

        // ChatGPT API 호출하여 AI 코칭 생성
        String coachingContent = chatGPTService.generateCoaching(analysisData);

        return CoachingResponseDTO.builder()
                .canAnalyze(true)
                .coachingContent(coachingContent)
                .analysisType(request.getAnalysisType())
                .questOrPomodoro(request.getQuestOrPomodoro())
                .analysisDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                .build();
    }

    @Override
    @Transactional
    public CoachingRecordDTO saveCoachingRecord(Long userId, String coachingContent, 
                                               String analysisType, String questOrPomodoro) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CoachingException(ErrorStatus.NO_SUCH_USER));

        CoachingRecord.AnalysisType analysisTypeEnum = convertToAnalysisType(analysisType);
        CoachingRecord.QuestOrPomodoro questOrPomodoroEnum = convertToQuestOrPomodoro(questOrPomodoro);

        CoachingRecord record = CoachingRecord.createCoachingRecord(
                user, analysisTypeEnum, questOrPomodoroEnum, coachingContent);

        CoachingRecord savedRecord = coachingRecordRepository.save(record);
        return CoachingConverter.toCoachingRecordDTO(savedRecord);
    }

    @Override
    public List<CoachingRecordDTO> getCoachingRecords(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<CoachingRecord> records = coachingRecordRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return CoachingConverter.toCoachingRecordDTOList(records);
    }



    @Override
    public AnalysisDataDTO collectAnalysisData(Long userId, String analysisType, String questOrPomodoro) {
        LocalDate today = LocalDate.now();
        LocalDate from = calculateFromDate(analysisType, today);
        LocalDate to = today;
        

        List<AnalysisDataDTO.QuestDataDTO> quests = null;
        List<AnalysisDataDTO.PomodoroDataDTO> pomodoros = null;

        if ("QUEST".equalsIgnoreCase(questOrPomodoro)) {
            quests = collectQuestData(userId, from, to);
        } else if ("POMODORO".equalsIgnoreCase(questOrPomodoro)) {
            pomodoros = collectPomodoroData(userId, from, to);
        }

        String analysisPeriod = from.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + 
                               " ~ " + to.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        return AnalysisDataDTO.builder()
                .analysisType(analysisType)
                .questOrPomodoro(questOrPomodoro)
                .quests(quests)
                .pomodoros(pomodoros)
                .analysisPeriod(analysisPeriod)
                .build();
    }

    // 분석 기간 계산
    private LocalDate calculateFromDate(String analysisType, LocalDate today) {
        switch (analysisType) {
            case "DAILY": return today.minusDays(6);  // D-7 ~ 오늘
            case "WEEKLY": return today.minusWeeks(4); // 4주 전 ~ 오늘
            case "MONTHLY": return today.minusMonths(11); // 12개월 전 ~ 오늘
            case "YEARLY": return today.minusYears(9); // 10개년 전 ~ 오늘
            default: return today.minusDays(6);
        }
    }

    // 퀘스트 데이터 수집 - QuestOccurrence 기반으로 실제 완료 상태 반영
    private List<AnalysisDataDTO.QuestDataDTO> collectQuestData(Long userId, LocalDate from, LocalDate to) {
        try {
            // QuestOccurrence에서 실제 완료 상태 데이터 조회
            List<ssuchaehwa.it_project.domain.quest.domain.entity.QuestOccurrence> occurrences = 
                questOccurrenceRepository.findAllByUserIdAndPeriodKeyBetweenOrderByPeriodKeyAsc(userId, from, to);
            
            return occurrences.stream()
                    .map(occurrence -> {
                        // 템플릿 정보는 Quest에서 가져오되, 완료 상태는 QuestOccurrence에서 가져옴
                        ssuchaehwa.it_project.domain.quest.domain.entity.Quest questTemplate = 
                            questRepository.findById(occurrence.getTemplateId()).orElse(null);
                        
                        return AnalysisDataDTO.QuestDataDTO.builder()
                                .questId(occurrence.getTemplateId())
                                .title(occurrence.getTitle()) // QuestOccurrence의 title 사용
                                .priority(questTemplate != null ? questTemplate.getPriority() : 1)
                                .startTime(occurrence.getExpectedStartTime() != null ? occurrence.getExpectedStartTime() : "00:00")
                                .endTime(occurrence.getExpectedEndTime() != null ? occurrence.getExpectedEndTime() : "00:00")
                                .startDate(occurrence.getPeriodKey().toString()) // period_key를 시작일로 사용
                                .dueDate(occurrence.getPeriodKey().toString()) // period_key를 마감일로 사용
                                .completionStatus(occurrence.getStatus()) // QuestOccurrence의 실제 완료 상태 사용
                                .questType(occurrence.getQuestType())
                                .build();
                    })
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("퀘스트 데이터 수집 중 오류", e);
            return List.of();
        }
    }

    // 뽀모도로 데이터 수집
    private List<AnalysisDataDTO.PomodoroDataDTO> collectPomodoroData(Long userId, LocalDate from, LocalDate to) {
        try {
            // 실제 PomodoroRepository를 통해 데이터 조회
            List<ssuchaehwa.it_project.domain.pomodoro.domain.entity.Pomodoro> pomodoros = pomodoroRepository.findAllByUser_IdAndStartTimeBetween(userId, from.atStartOfDay(), to.atTime(23, 59, 59));
            
            // 일자별로 그룹화하여 dailyCount 계산
            Map<LocalDate, List<ssuchaehwa.it_project.domain.pomodoro.domain.entity.Pomodoro>> groupedByDate = pomodoros.stream()
                    .collect(Collectors.groupingBy(p -> p.getStartTime().toLocalDate()));
            
            return groupedByDate.entrySet().stream()
                    .map(entry -> {
                        LocalDate date = entry.getKey();
                        List<ssuchaehwa.it_project.domain.pomodoro.domain.entity.Pomodoro> dailyPomodoros = entry.getValue();
                        
                        // 해당 일자의 총 수행 시간 계산
                        long totalMinutes = dailyPomodoros.stream()
                                .mapToLong(p -> {
                                    if (p.getStartTime() != null && p.getEndTime() != null) {
                                        return java.time.Duration.between(p.getStartTime(), p.getEndTime()).toMinutes();
                                    }
                                    return 0;
                                })
                                .sum();
                        
                        return AnalysisDataDTO.PomodoroDataDTO.builder()
                                .performanceDate(date.toString())
                                .durationMinutes((int) totalMinutes)
                                .dailyCount(dailyPomodoros.size())
                                .build();
                    })
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("뽀모도로 데이터 수집 중 오류", e);
            return List.of();
        }
    }

    // String을 AnalysisType enum으로 변환
    private CoachingRecord.AnalysisType convertToAnalysisType(String analysisType) {
        try {
            return CoachingRecord.AnalysisType.valueOf(analysisType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CoachingException(ErrorStatus._BAD_REQUEST);
        }
    }

    // String을 QuestOrPomodoro enum으로 변환
    private CoachingRecord.QuestOrPomodoro convertToQuestOrPomodoro(String questOrPomodoro) {
        try {
            return CoachingRecord.QuestOrPomodoro.valueOf(questOrPomodoro.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CoachingException(ErrorStatus._BAD_REQUEST);
        }
    }
}
