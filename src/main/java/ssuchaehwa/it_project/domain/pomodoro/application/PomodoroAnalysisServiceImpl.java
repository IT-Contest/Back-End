package ssuchaehwa.it_project.domain.pomodoro.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ssuchaehwa.it_project.domain.pomodoro.domain.repository.PomodoroAnalysisRepository;
import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroAnalysisResponseDTO;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PomodoroAnalysisServiceImpl implements PomodoroAnalysisService {

    private final PomodoroAnalysisRepository pomodoroAnalysisRepository;

    @Override
    public List<PomodoroAnalysisResponseDTO.Daily> getDaily(Long userId, LocalDate from, LocalDate to) {
        log.info("뽀모도로 일일 분석 요청 - 사용자: {}, 기간: {} ~ {}", userId, from, to);
        
        // 1) 지정된 기간 버킷 선생성(0으로 채움)
        Map<LocalDate, int[]> bucket = new LinkedHashMap<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            bucket.put(d, new int[]{0, 0}); // [completed, total]
        }
        
        // 2) 실제 데이터 조회 및 버킷에 반영
        List<Object[]> rows = pomodoroAnalysisRepository.countDaily(userId, from, to);
        for (Object[] row : rows) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            int completed = ((Number) row[1]).intValue();
            int total = ((Number) row[2]).intValue();
            if (bucket.containsKey(date)) {
                bucket.put(date, new int[]{completed, total});
            }
        }
        
        // 3) DTO 변환(라벨은 MM-DD 형식으로 변경)
        return bucket.entrySet().stream()
                .map(e -> new PomodoroAnalysisResponseDTO.Daily(
                        formatDailyLabel(e.getKey()),  // "08-31" 형식
                        e.getValue()[0],
                        e.getValue()[1]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<PomodoroAnalysisResponseDTO.Weekly> getWeekly(Long userId, LocalDate from, LocalDate to) {
        log.info("뽀모도로 주간 분석 요청 - 사용자: {}, 기간: {} ~ {}", userId, from, to);
        
        // 날짜 검증
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("시작일은 종료일보다 늦을 수 없습니다: from=" + from + ", to=" + to);
        }

        var rows = pomodoroAnalysisRepository.countWeekly(userId, from, to);

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
                .map(e -> new PomodoroAnalysisResponseDTO.Weekly(
                        label.getOrDefault(e.getKey(), String.valueOf(e.getKey())),
                        e.getValue()[0],
                        e.getValue()[1]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<PomodoroAnalysisResponseDTO.Monthly> getMonthly(Long userId, LocalDate from, LocalDate to) {
        log.info("뽀모도로 월간 분석 요청 - 사용자: {}, 기간: {} ~ {}", userId, from, to);
        
        YearMonth fromMonth = YearMonth.from(from);
        YearMonth toMonth = YearMonth.from(to);
        
        // 월별 버킷 0 채우기
        Map<YearMonth, int[]> bucket = new LinkedHashMap<>();
        for (YearMonth ym = fromMonth; !ym.isAfter(toMonth); ym = ym.plusMonths(1)) {
            bucket.put(ym, new int[]{0, 0});
        }
        
        // 실제 데이터 조회 및 월별로 집계
        List<Object[]> rows = pomodoroAnalysisRepository.countMonthly(userId, from, to);
        for (Object[] row : rows) {
            String ymStr = row[0].toString(); // "YYYY-MM"
            int completed = ((Number) row[1]).intValue();
            int total = ((Number) row[2]).intValue();
            YearMonth ym = YearMonth.parse(ymStr);
            if (bucket.containsKey(ym)) {
                bucket.put(ym, new int[]{completed, total});
            }
        }
        
        // 3) DTO 변환 (라벨은 "MM월")
        return bucket.entrySet().stream()
                .map(e -> new PomodoroAnalysisResponseDTO.Monthly(
                        formatMonthlyLabel(e.getKey()),
                        e.getValue()[0],
                        e.getValue()[1]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<PomodoroAnalysisResponseDTO.Yearly> getYearly(Long userId, LocalDate from, LocalDate to) {
        log.info("뽀모도로 연간 분석 요청 - 사용자: {}, 기간: {} ~ {}", userId, from, to);
        
        int fromYear = from.getYear();
        int toYear = to.getYear();
        
        // 연도별 버킷 0 채우기
        Map<Integer, int[]> bucket = new LinkedHashMap<>();
        for (int y = fromYear; y <= toYear; y++) {
            bucket.put(y, new int[]{0, 0});
        }
        
        // 실제 데이터 조회 및 년별로 집계
        List<Object[]> rows = pomodoroAnalysisRepository.countYearly(userId, from, to);
        for (Object[] row : rows) {
            int year = ((Number) row[0]).intValue();
            int completed = ((Number) row[1]).intValue();
            int total = ((Number) row[2]).intValue();
            if (bucket.containsKey(year)) {
                bucket.put(year, new int[]{completed, total});
            }
        }
        
        // 3) DTO 변환 (라벨은 "YYYY년")
        return bucket.entrySet().stream()
                .map(e -> new PomodoroAnalysisResponseDTO.Yearly(
                        formatYearlyLabel(e.getKey()),
                        e.getValue()[0],
                        e.getValue()[1]
                ))
                .collect(Collectors.toList());
    }
    
    // ========== 유틸리티 메서드들 ==========
    
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
}
