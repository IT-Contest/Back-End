package ssuchaehwa.it_project.domain.quest.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssuchaehwa.it_project.domain.quest.domain.repository.QuestAnalysisRepository;
import ssuchaehwa.it_project.domain.quest.domain.repository.QuestOccurrenceRepository;
import ssuchaehwa.it_project.domain.quest.domain.entity.QuestOccurrence;
import ssuchaehwa.it_project.domain.quest.dto.AnalysisResponseDTO;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestAnalysisServiceImpl implements QuestAnalysisService {

    private final QuestAnalysisRepository questAnalysisRepository;
    private final QuestOccurrenceRepository questOccurrenceRepository;

    @Override
    public List<AnalysisResponseDTO.Daily> getDaily(Long userId, LocalDate from, LocalDate to, String questType) {
        // 날짜 검증
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("시작일은 종료일보다 늦을 수 없습니다: from=" + from + ", to=" + to);
        }

        var rows = questAnalysisRepository.countDaily(userId, from, to, questType);

        // 1) from~to 날짜 버킷 선생성(0으로 채움)
        java.util.Map<LocalDate, int[]> bucket = new java.util.LinkedHashMap<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            bucket.put(d, new int[]{0, 0}); // [completed, total]
        }

        // 2) 쿼리 결과 반영
        for (Object[] row : rows) {
            // row[0]은 DATE(period_key)
            LocalDate day;
            Object dObj = row[0];
            if (dObj instanceof java.sql.Date sqlDate) {
                day = sqlDate.toLocalDate();
            } else {
                day = LocalDate.parse(dObj.toString());
            }
            int completed = ((Number) row[1]).intValue();
            int total     = ((Number) row[2]).intValue();
            // 버킷에 이미 존재하는 날짜만 업데이트하여 범위 유지
            if (bucket.containsKey(day)) {
                bucket.put(day, new int[]{completed, total});
            }
        }

        // 3) DTO 변환(라벨은 yyyy-MM-dd 그대로 반환; 프론트에서 포맷 가능)
        return bucket.entrySet().stream()
                .map(e -> new AnalysisResponseDTO.Daily(
                        e.getKey().toString(),
                        e.getValue()[0],
                        e.getValue()[1]
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<AnalysisResponseDTO.Weekly> getWeekly(Long userId, LocalDate from, LocalDate to, String questType) {
        // 날짜 검증
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("시작일은 종료일보다 늦을 수 없습니다: from=" + from + ", to=" + to);
        }

        var rows = questAnalysisRepository.countWeekly(userId, from, to, questType);

        // 1) 현재 날짜(오늘)를 기준으로 현재 주차부터 과거 4주차까지 총 5개의 주차 버킷을 생성합니다.
        var weekField = java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR;
        var yearField = java.time.temporal.IsoFields.WEEK_BASED_YEAR;

        // 현재 날짜(오늘)가 포함된 주의 월요일을 찾습니다.
        LocalDate today = LocalDate.now();
        LocalDate currentMonday = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        // 4주 전의 월요일을 시작점으로 설정합니다.
        LocalDate firstMonday = currentMonday.minusWeeks(4);

        java.util.Map<Integer, int[]> bucket = new java.util.LinkedHashMap<>();
        java.util.Map<Integer, String> label = new java.util.HashMap<>();

        // firstMonday부터 currentMonday까지 1주씩 증가하며 5개의 버킷을 생성합니다.
        for (LocalDate cursor = firstMonday; !cursor.isAfter(currentMonday); cursor = cursor.plusWeeks(1)) {
            int isoYear = cursor.get(yearField);
            int isoWeek = cursor.get(weekField);
            int key = isoYear * 100 + isoWeek; // MySQL YEARWEEK와 동일한 키
            bucket.put(key, new int[]{0, 0}); // putIfAbsent 대신 put을 사용해도 무방합니다.
            label.put(key, isoYear + "-W" + String.format("%02d", isoWeek));
        }

        // 2) 쿼리 결과 반영 (row[0]=YEARWEEK, row[1]=completed, row[2]=total)
        for (Object[] row : rows) {
            int key        = ((Number) row[0]).intValue();
            int completed  = ((Number) row[1]).intValue();
            int total      = ((Number) row[2]).intValue();
            // 버킷에 이미 존재하는 key만 업데이트하여 순서 유지
            if (bucket.containsKey(key)) {
                bucket.put(key, new int[]{completed, total});
            }
        }

        // 3) DTO 변환(라벨은 "YYYY-Www")
        return bucket.entrySet().stream()
                .map(e -> new AnalysisResponseDTO.Weekly(
                        label.getOrDefault(e.getKey(), String.valueOf(e.getKey())),
                        e.getValue()[0],
                        e.getValue()[1]
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<AnalysisResponseDTO.Monthly> getMonthly(Long userId, LocalDate from, LocalDate to, String questType) {
        var rows = questAnalysisRepository.countMonthly(userId, from, to, questType);

        // 1) YearMonth 버킷 0 채우기
        java.time.YearMonth start = java.time.YearMonth.from(from);
        java.time.YearMonth end   = java.time.YearMonth.from(to);

        java.util.Map<java.time.YearMonth, int[]> bucket = new java.util.LinkedHashMap<>();
        for (java.time.YearMonth ym = start; !ym.isAfter(end); ym = ym.plusMonths(1)) {
            bucket.put(ym, new int[]{0, 0});
        }

        // 2) 결과 반영 (row[0] = "YYYY-MM")
        for (Object[] row : rows) {
            String ymStr   = row[0].toString(); // "YYYY-MM"
            int completed  = ((Number) row[1]).intValue();
            int total      = ((Number) row[2]).intValue();
            java.time.YearMonth ym = java.time.YearMonth.parse(ymStr);
            bucket.put(ym, new int[]{completed, total});
        }

        // 3) DTO 변환 (라벨은 "YYYY-MM", 프론트에서 "MM월" 등으로 포맷 가능)
        return bucket.entrySet().stream()
                .map(e -> new AnalysisResponseDTO.Monthly(
                        e.getKey().toString(),
                        e.getValue()[0],
                        e.getValue()[1]
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<AnalysisResponseDTO.Yearly> getYearly(Long userId, LocalDate from, LocalDate to, String questType) {
        var rows = questAnalysisRepository.countYearly(userId, from, to, questType);

        // 1) 연도 버킷 0 채우기
        int startYear = from.getYear();
        int endYear   = to.getYear();
        java.util.Map<Integer, int[]> bucket = new java.util.LinkedHashMap<>();
        for (int y = startYear; y <= endYear; y++) {
            bucket.put(y, new int[]{0, 0});
        }

        // 2) 결과 반영 (row[0] = YEAR 정수)
        for (Object[] row : rows) {
            int year       = ((Number) row[0]).intValue();
            int completed  = ((Number) row[1]).intValue();
            int total      = ((Number) row[2]).intValue();
            bucket.put(year, new int[]{completed, total});
        }

        // 3) DTO 변환 (라벨은 "YYYY")
        return bucket.entrySet().stream()
                .map(e -> new AnalysisResponseDTO.Yearly(
                        String.valueOf(e.getKey()),
                        e.getValue()[0],
                        e.getValue()[1]
                ))
                .collect(java.util.stream.Collectors.toList());
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