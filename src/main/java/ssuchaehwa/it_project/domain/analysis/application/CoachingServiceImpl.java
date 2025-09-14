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

        // 코칭 기록 저장
        CoachingRecordDTO savedRecord = saveCoachingRecord(userId, coachingContent, 
                request.getAnalysisType(), request.getQuestOrPomodoro());

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
        
        log.info("=== 분석 데이터 수집 시작 ===");
        log.info("사용자 ID: {}, 분석 타입: {}, 분석 대상: {}", userId, analysisType, questOrPomodoro);
        log.info("분석 기간: {} ~ {}", from, to);

        List<AnalysisDataDTO.QuestDataDTO> quests = null;
        List<AnalysisDataDTO.PomodoroDataDTO> pomodoros = null;

        if ("QUEST".equals(questOrPomodoro)) {
            quests = collectQuestData(userId, from, to);
            log.info("퀘스트 데이터 수집 완료: {}개", quests != null ? quests.size() : 0);
        } else if ("POMODORO".equals(questOrPomodoro)) {
            pomodoros = collectPomodoroData(userId, from, to);
            log.info("뽀모도로 데이터 수집 완료: {}개", pomodoros != null ? pomodoros.size() : 0);
        }

        String analysisPeriod = from.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + 
                               " ~ " + to.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        AnalysisDataDTO result = AnalysisDataDTO.builder()
                .analysisType(analysisType)
                .questOrPomodoro(questOrPomodoro)
                .quests(quests)
                .pomodoros(pomodoros)
                .analysisPeriod(analysisPeriod)
                .build();
        
        log.info("=== 분석 데이터 수집 완료 ===");
        log.info("최종 결과: {}", result);
        
        return result;
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

    // 퀘스트 데이터 수집
    private List<AnalysisDataDTO.QuestDataDTO> collectQuestData(Long userId, LocalDate from, LocalDate to) {
        try {
            log.info("퀘스트 데이터 수집 - 사용자: {}, 기간: {} ~ {}", userId, from, to);
            
            // 실제 QuestRepository를 통해 데이터 조회
            List<ssuchaehwa.it_project.domain.quest.domain.entity.Quest> quests = questRepository.findAllByUserId(userId);
            log.info("조회된 퀘스트 개수: {}", quests.size());
            
            List<AnalysisDataDTO.QuestDataDTO> result = quests.stream()
                    .map(quest -> {
                        AnalysisDataDTO.QuestDataDTO dto = AnalysisDataDTO.QuestDataDTO.builder()
                                .questId(quest.getId())
                                .title(quest.getTitle())
                                .priority(quest.getPriority())
                                .startTime(quest.getStartTime() != null ? quest.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "00:00")
                                .endTime(quest.getEndTime() != null ? quest.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "00:00")
                                .startDate(quest.getStartDate() != null ? quest.getStartDate().toString() : from.toString())
                                .dueDate(quest.getDueDate() != null ? quest.getDueDate().toString() : to.toString())
                                .completionStatus(quest.getCompletionStatus().toString())
                                .questType(quest.getQuestType().toString())
                                .build();
                        
                        log.info("퀘스트 데이터 변환: ID={}, 제목={}, 우선순위={}, 시작시간={}, 종료시간={}, 시작일={}, 마감일={}, 완료상태={}, 타입={}", 
                                dto.getQuestId(), dto.getTitle(), dto.getPriority(), dto.getStartTime(), dto.getEndTime(), 
                                dto.getStartDate(), dto.getDueDate(), dto.getCompletionStatus(), dto.getQuestType());
                        
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            log.info("최종 퀘스트 DTO 개수: {}", result.size());
            return result;
                    
        } catch (Exception e) {
            log.error("퀘스트 데이터 수집 중 오류", e);
            return List.of();
        }
    }

    // 뽀모도로 데이터 수집
    private List<AnalysisDataDTO.PomodoroDataDTO> collectPomodoroData(Long userId, LocalDate from, LocalDate to) {
        try {
            log.info("뽀모도로 데이터 수집 - 사용자: {}, 기간: {} ~ {}", userId, from, to);
            
            // 실제 PomodoroRepository를 통해 데이터 조회
            List<ssuchaehwa.it_project.domain.pomodoro.domain.entity.Pomodoro> pomodoros = pomodoroRepository.findAllByUser_IdAndStartTimeBetween(userId, from.atStartOfDay(), to.atTime(23, 59, 59));
            log.info("조회된 뽀모도로 개수: {}", pomodoros.size());
            
            // 일자별로 그룹화하여 dailyCount 계산
            Map<LocalDate, List<ssuchaehwa.it_project.domain.pomodoro.domain.entity.Pomodoro>> groupedByDate = pomodoros.stream()
                    .collect(Collectors.groupingBy(p -> p.getStartTime().toLocalDate()));
            
            List<AnalysisDataDTO.PomodoroDataDTO> result = groupedByDate.entrySet().stream()
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
                        
                        AnalysisDataDTO.PomodoroDataDTO dto = AnalysisDataDTO.PomodoroDataDTO.builder()
                                .performanceDate(date.toString())
                                .durationMinutes((int) totalMinutes)
                                .dailyCount(dailyPomodoros.size())
                                .build();
                        
                        log.info("뽀모도로 데이터 변환: 수행일자={}, 총시간={}분, 일일횟수={}", 
                                dto.getPerformanceDate(), dto.getDurationMinutes(), dto.getDailyCount());
                        
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            log.info("최종 뽀모도로 DTO 개수: {}", result.size());
            return result;
                    
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
