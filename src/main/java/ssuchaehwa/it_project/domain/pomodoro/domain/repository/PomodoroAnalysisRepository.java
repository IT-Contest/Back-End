package ssuchaehwa.it_project.domain.pomodoro.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PomodoroAnalysisRepository extends JpaRepository<ssuchaehwa.it_project.domain.pomodoro.domain.entity.Pomodoro, Long> {

    // 일일 분석: D-7 ~ 오늘까지
    @Query(value = """
            SELECT 
                DATE(start_time) as d,
                COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) as completed,
                COUNT(*) as total
            FROM focus_session 
            WHERE user_id = :userId 
                AND DATE(start_time) BETWEEN :from AND :to
            GROUP BY DATE(start_time)
            ORDER BY d
            """, nativeQuery = true)
    List<Object[]> countDaily(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    // 주간 분석: 이번 달 주차별
    @Query(value = """
            SELECT 
                YEARWEEK(start_time, 1) as week_key,
                COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) as completed,
                COUNT(*) as total
            FROM focus_session 
            WHERE user_id = :userId 
                AND DATE(start_time) BETWEEN :from AND :to
            GROUP BY YEARWEEK(start_time, 1)
            ORDER BY week_key
            """, nativeQuery = true)
    List<Object[]> countWeekly(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    // 월간 분석: 이번 달 포함 전월 12개
    @Query(value = """
            SELECT 
                DATE_FORMAT(start_time, '%Y-%m') as month_key,
                COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) as completed,
                COUNT(*) as total
            FROM focus_session 
            WHERE user_id = :userId 
                AND DATE(start_time) BETWEEN :from AND :to
            GROUP BY DATE_FORMAT(start_time, '%Y-%m')
            ORDER BY month_key
            """, nativeQuery = true)
    List<Object[]> countMonthly(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    // 연간 분석: 이번 년도 포함 전년 10개
    @Query(value = """
            SELECT 
                YEAR(start_time) as year_key,
                COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) as completed,
                COUNT(*) as total
            FROM focus_session 
            WHERE user_id = :userId 
                AND DATE(start_time) BETWEEN :from AND :to
            GROUP BY YEAR(start_time)
            ORDER BY year_key
            """, nativeQuery = true)
    List<Object[]> countYearly(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);
}
