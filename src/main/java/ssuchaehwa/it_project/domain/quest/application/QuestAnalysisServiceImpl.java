package ssuchaehwa.it_project.domain.quest.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssuchaehwa.it_project.domain.quest.domain.repository.QuestAnalysisRepository;
import ssuchaehwa.it_project.domain.quest.domain.repository.QuestOccurrenceRepository;
import ssuchaehwa.it_project.domain.quest.domain.entity.QuestOccurrence;
import ssuchaehwa.it_project.domain.quest.dto.AnalysisResponseDTO;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestAnalysisServiceImpl implements QuestAnalysisService {

    private final QuestAnalysisRepository questAnalysisRepository;
    private final QuestOccurrenceRepository questOccurrenceRepository;

    @Override
    public List<AnalysisResponseDTO.Daily> getDaily(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(6);  // D-7
        LocalDate to = today;                 // 오늘
        
        var rows = questAnalysisRepository.countDaily(userId, from, to);

        // 1) D-7 ~ 오늘까지 버킷 선생성(0으로 채움, 총 7개)
        java.util.Map<LocalDate, int[]> bucket = new java.util.LinkedHashMap<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            bucket.put(d, new int[]{0, 0}); // [completed, total]
        }

        // 2) 쿼리 결과 반영
        for (Object[] row : rows) {
            // row[0]은 DATE(period_key) 결과. 타입 안전 처리
            LocalDate day;
            Object dObj = row[0];
            if (dObj instanceof java.sql.Date sqlDate) {
                day = sqlDate.toLocalDate();
            } else {
                day = LocalDate.parse(dObj.toString());
            }
            int completed = ((Number) row[1]).intValue();
            int total     = ((Number) row[2]).intValue();
            bucket.put(day, new int[]{completed, total});
        }

        // 3) DTO 변환(라벨은 MM-DD 형식으로 변경)
        return bucket.entrySet().stream()
                .map(e -> new AnalysisResponseDTO.Daily(
                        formatDailyLabel(e.getKey()),  // "08-31" 형식
                        e.getValue()[0],
                        e.getValue()[1]
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<AnalysisResponseDTO.Weekly> getWeekly(Long userId) {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        
        // 이번 달 1일부터 마지막 날까지
        LocalDate monthStart = currentMonth.atDay(1);
        LocalDate monthEnd = currentMonth.atEndOfMonth();
        
        var rows = questAnalysisRepository.countWeekly(userId, monthStart, monthEnd);

        // 1) MySQL YEARWEEK(...,1) = ISO Week 기준. 자바에서도 ISO 주차로 맞춰 키 생성
        var weekField = java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR;
        var yearField = java.time.temporal.IsoFields.WEEK_BASED_YEAR;

        // from을 주의 시작(월요일)로 맞춤
        java.time.DayOfWeek firstDay = java.time.DayOfWeek.MONDAY;
        LocalDate cursor = monthStart.minusDays((monthStart.getDayOfWeek().getValue() - firstDay.getValue() + 7) % 7);

        // 1) 5개 주차 버킷 선생성
        java.util.Map<Integer, int[]> bucket = new java.util.LinkedHashMap<>();
        java.util.Map<Integer, String> label  = new java.util.HashMap<>();
        
        // 1주차: 1-7일, 2주차: 8-14일, 3주차: 15-21일, 4주차: 22-28일, 5주차: 29일~
        int[] weekRanges = {1, 8, 15, 22, 29};
        for (int i = 0; i < 5; i++) {
            int weekNum = i + 1;
            bucket.put(weekNum, new int[]{0, 0});
            label.put(weekNum, currentMonth.getMonthValue() + "월 " + weekNum + "주차");
        }

        // 2) 쿼리 결과 반영 (row[0]=YEARWEEK, row[1]=completed, row[2]=total)
        for (Object[] row : rows) {
            int key        = ((Number) row[0]).intValue();
            int completed  = ((Number) row[1]).intValue();
            int total      = ((Number) row[2]).intValue();
            
            // YEARWEEK를 주차 번호로 변환 (1~5)
            int weekNum = getWeekNumberFromYearWeek(key, currentMonth);
            if (weekNum >= 1 && weekNum <= 5) {
                bucket.put(weekNum, new int[]{completed, total});
            }
        }

        // 3) DTO 변환(라벨은 "MM월 N주차")
        return bucket.entrySet().stream()
                .map(e -> new AnalysisResponseDTO.Weekly(
                        label.getOrDefault(e.getKey(), String.valueOf(e.getKey())),
                        e.getValue()[0],
                        e.getValue()[1]
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<AnalysisResponseDTO.Monthly> getMonthly(Long userId) {
        LocalDate today = LocalDate.now();
        YearMonth current = YearMonth.from(today);
        
        // 이번 달 포함 전월 12개
        YearMonth from = current.minusMonths(11);
        YearMonth to = current;
        
        var rows = questAnalysisRepository.countMonthly(userId, from.atDay(1), to.atEndOfMonth());

        // 1) 12개월 버킷 0 채우기
        java.util.Map<YearMonth, int[]> bucket = new java.util.LinkedHashMap<>();
        for (YearMonth ym = from; !ym.isAfter(to); ym = ym.plusMonths(1)) {
            bucket.put(ym, new int[]{0, 0});
        }

        // 2) 결과 반영 (row[0] = "YYYY-MM")
        for (Object[] row : rows) {
            String ymStr   = row[0].toString(); // "YYYY-MM"
            int completed  = ((Number) row[1]).intValue();
            int total      = ((Number) row[2]).intValue();
            YearMonth ym = YearMonth.parse(ymStr);
            bucket.put(ym, new int[]{completed, total});
        }

        // 3) DTO 변환 (라벨은 "MM월")
        return bucket.entrySet().stream()
                .map(e -> new AnalysisResponseDTO.Monthly(
                        formatMonthlyLabel(e.getKey()),
                        e.getValue()[0],
                        e.getValue()[1]
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<AnalysisResponseDTO.Yearly> getYearly(Long userId) {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        
        // 이번 년도 포함 전년 10개
        int fromYear = currentYear - 9;
        int toYear = currentYear;
        
        var rows = questAnalysisRepository.countYearly(userId, 
            LocalDate.of(fromYear, 1, 1), 
            LocalDate.of(toYear, 12, 31));

        // 1) 10개년 버킷 0 채우기
        java.util.Map<Integer, int[]> bucket = new java.util.LinkedHashMap<>();
        for (int y = fromYear; y <= toYear; y++) {
            bucket.put(y, new int[]{0, 0});
        }

        // 2) 결과 반영 (row[0] = YEAR 정수)
        for (Object[] row : rows) {
            int year       = ((Number) row[0]).intValue();
            int completed  = ((Number) row[1]).intValue();
            int total      = ((Number) row[2]).intValue();
            bucket.put(year, new int[]{completed, total});
        }

        // 3) DTO 변환 (라벨은 "YYYY년")
        return bucket.entrySet().stream()
                .map(e -> new AnalysisResponseDTO.Yearly(
                        formatYearlyLabel(e.getKey()),
                        e.getValue()[0],
                        e.getValue()[1]
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    // 일일 라벨 포맷: "MM-DD"
    private String formatDailyLabel(LocalDate date) {
        return String.format("%02d-%02d", date.getMonthValue(), date.getDayOfMonth());
    }

    // 월간 라벨 포맷: "MM월"
    private String formatMonthlyLabel(YearMonth yearMonth) {
        return yearMonth.getMonthValue() + "월";
    }

    // 연간 라벨 포맷: "YYYY년"
    private String formatYearlyLabel(int year) {
        return year + "년";
    }

    // YEARWEEK를 주차 번호로 변환 (1~5)
    private int getWeekNumberFromYearWeek(int yearWeek, YearMonth currentMonth) {
        // YEARWEEK는 YYYYWW 형태 (예: 202401)
        int year = yearWeek / 100;
        int week = yearWeek % 100;
        
        // 이번 달의 주차 계산
        if (year == currentMonth.getYear()) {
            // 1주차: 1-7일, 2주차: 8-14일, 3주차: 15-21일, 4주차: 22-28일, 5주차: 29일~
            LocalDate firstDay = currentMonth.atDay(1);
            int dayOfWeek = firstDay.getDayOfWeek().getValue();
            int firstWeekStart = 1 + (7 - dayOfWeek + 1) % 7;
            
            if (week == 1) return 1;
            else if (week == 2) return 2;
            else if (week == 3) return 3;
            else if (week == 4) return 4;
            else if (week == 5) return 5;
        }
        
        return 1; // 기본값
    }

    /**
     * 앵커 날짜: startDate가 있으면 startDate, 없으면 createdAt(LocalDate)
     */
    public LocalDate anchorDateOf(QuestOccurrence occurrence) {
        // QuestOccurrence는 이미 period_key가 계산되어 있으므로 직접 사용
        return occurrence.getPeriodKey();
    }

    /**
     * 앵커 기반 현재 기간의 period_key 계산
     * - DAILY : today
     * - WEEKLY: anchor + 7*k (<= today) 중 최대값
     * - MONTHLY: 매월 anchor의 day를 유지하되, 해당 달에 그 일이 없으면 말일로 보정
     * - YEARLY: 매년 anchor의 월/일 (존재하지 않는 날짜는 말일 보정)
     */
    public LocalDate currentPeriodKeyFromAnchor(String questType, LocalDate anchor, LocalDate today) {
        switch (questType) {
            case "DAILY":
                return today;
            case "WEEKLY": {
                long days = ChronoUnit.DAYS.between(anchor, today);
                if (days < 0) return anchor; // 아직 시작 전이면 앵커를 그대로
                long weeks = days / 7; // floor
                return anchor.plusDays(weeks * 7);
            }
            case "MONTHLY": {
                int anchorDay = anchor.getDayOfMonth();
                int y = today.getYear();
                int m = today.getMonthValue();
                int lastDay = java.time.YearMonth.of(y, m).lengthOfMonth();
                int targetDay = Math.min(anchorDay, lastDay);
                // today가 anchor 월주기에 도달하지 않았으면, 직전 주기의 키를 사용하도록 보정
                LocalDate candidate = LocalDate.of(y, m, targetDay);
                if (candidate.isAfter(today)) {
                    // 직전 달로 이동
                    java.time.YearMonth prev = java.time.YearMonth.of(y, m).minusMonths(1);
                    int prevLast = prev.lengthOfMonth();
                    int prevDay = Math.min(anchorDay, prevLast);
                    candidate = LocalDate.of(prev.getYear(), prev.getMonthValue(), prevDay);
                }
                return candidate;
            }
            case "YEARLY": {
                int anchorMonth = anchor.getMonthValue();
                int anchorDay = anchor.getDayOfMonth();
                int y = today.getYear();
                int lastDay = java.time.YearMonth.of(y, anchorMonth).lengthOfMonth();
                int targetDay = Math.min(anchorDay, lastDay);
                LocalDate candidate = LocalDate.of(y, anchorMonth, targetDay);
                if (candidate.isAfter(today)) {
                    // 직전 해로 이동
                    int py = y - 1;
                    int plast = java.time.YearMonth.of(py, anchorMonth).lengthOfMonth();
                    int pday = Math.min(anchorDay, plast);
                    candidate = LocalDate.of(py, anchorMonth, pday);
                }
                return candidate;
            }
            default:
                return today;
        }
    }

    // 각 퀘스트의 "현재 기간" occurrence가 없으면 INCOMPLETE로 생성
    public void ensureCurrentOccurrences(List<QuestOccurrence> occurrences, Long userId, LocalDate today) {
        for (QuestOccurrence occ : occurrences) {
            LocalDate pk = currentPeriodKeyFromAnchor(occ.getQuestType(), occ.getPeriodKey(), today);
            boolean exists = questOccurrenceRepository.existsByTemplateIdAndPeriodKey(occ.getTemplateId(), pk);
            if (!exists) {
                QuestOccurrence newOcc = QuestOccurrence.builder()
                        .templateId(occ.getTemplateId())
                        .userId(userId)
                        .questType(occ.getQuestType())
                        .periodKey(pk)
                        .status("INCOMPLETE")
                        .expectedStartTime(occ.getExpectedStartTime())
                        .expectedEndTime(occ.getExpectedEndTime())
                        .title(occ.getTitle())
                        .build();
                questOccurrenceRepository.save(newOcc);
            }
        }
    }
}
