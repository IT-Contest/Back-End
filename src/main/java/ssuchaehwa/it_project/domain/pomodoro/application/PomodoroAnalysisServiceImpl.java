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
    public List<PomodoroAnalysisResponseDTO.Daily> getDaily(Long userId) {
        log.info("뽀모도로 일일 분석 요청 - 사용자: {}", userId);
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(6);  // D-7
        LocalDate to = today;                 // 오늘
        
        // 1) D-7 ~ 오늘까지 버킷 선생성(0으로 채움, 총 7개)
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
    public List<PomodoroAnalysisResponseDTO.Weekly> getWeekly(Long userId) {
        log.info("뽀모도로 주간 분석 요청 - 사용자: {}", userId);
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        
        // 이번 달 1일부터 마지막 날까지
        LocalDate monthStart = currentMonth.atDay(1);
        LocalDate monthEnd = currentMonth.atEndOfMonth();
        
        // 1) 5개 주차 버킷 선생성
        Map<Integer, int[]> bucket = new LinkedHashMap<>();
        Map<Integer, String> label = new LinkedHashMap<>();
        
        // 1주차: 1-7일, 2주차: 8-14일, 3주차: 15-21일, 4주차: 22-28일, 5주차: 29일~
        for (int i = 0; i < 5; i++) {
            int weekNum = i + 1;
            bucket.put(weekNum, new int[]{0, 0});
            label.put(weekNum, currentMonth.getMonthValue() + "월 " + weekNum + "주차");
        }
        
        // 2) 실제 데이터 조회 및 주차별로 집계
        List<Object[]> rows = pomodoroAnalysisRepository.countWeekly(userId, monthStart, monthEnd);
        for (Object[] row : rows) {
            int yearWeek = ((Number) row[0]).intValue();
            int completed = ((Number) row[1]).intValue();
            int total = ((Number) row[2]).intValue();
            
            // YEARWEEK를 주차 번호로 변환 (1~5)
            int weekNum = getWeekNumberFromYearWeek(yearWeek, currentMonth);
            if (weekNum >= 1 && weekNum <= 5) {
                bucket.put(weekNum, new int[]{completed, total});
            }
        }
        
        // 3) DTO 변환(라벨은 "MM월 N주차")
        return bucket.entrySet().stream()
                .map(e -> new PomodoroAnalysisResponseDTO.Weekly(
                        label.get(e.getKey()),
                        e.getValue()[0],
                        e.getValue()[1]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<PomodoroAnalysisResponseDTO.Monthly> getMonthly(Long userId) {
        log.info("뽀모도로 월간 분석 요청 - 사용자: {}", userId);
        LocalDate today = LocalDate.now();
        YearMonth current = YearMonth.from(today);
        
        // 이번 달 포함 전월 12개
        YearMonth from = current.minusMonths(11);
        YearMonth to = current;
        
        // 1) 12개월 버킷 0 채우기
        Map<YearMonth, int[]> bucket = new LinkedHashMap<>();
        for (YearMonth ym = from; !ym.isAfter(to); ym = ym.plusMonths(1)) {
            bucket.put(ym, new int[]{0, 0});
        }
        
        // 2) 실제 데이터 조회 및 월별로 집계
        List<Object[]> rows = pomodoroAnalysisRepository.countMonthly(userId, from.atDay(1), to.atEndOfMonth());
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
    public List<PomodoroAnalysisResponseDTO.Yearly> getYearly(Long userId) {
        log.info("뽀모도로 연간 분석 요청 - 사용자: {}", userId);
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        
        // 이번 년도 포함 전년 10개
        int fromYear = currentYear - 9;
        int toYear = currentYear;
        
        // 1) 10개년 버킷 0 채우기
        Map<Integer, int[]> bucket = new LinkedHashMap<>();
        for (int y = fromYear; y <= toYear; y++) {
            bucket.put(y, new int[]{0, 0});
        }
        
        // 2) 실제 데이터 조회 및 년별로 집계
        List<Object[]> rows = pomodoroAnalysisRepository.countYearly(userId, 
            LocalDate.of(fromYear, 1, 1), LocalDate.of(toYear, 12, 31));
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
}
