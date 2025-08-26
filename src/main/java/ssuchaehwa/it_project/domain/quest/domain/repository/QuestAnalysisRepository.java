package ssuchaehwa.it_project.domain.quest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ssuchaehwa.it_project.domain.quest.domain.entity.QuestOccurrence;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface QuestAnalysisRepository extends JpaRepository<QuestOccurrence, Long> {

    // 일 단위 집계 (기간: :from ~ :to)
    @Query(value = """
            SELECT 
                DATE(period_key) AS d,
                SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed,
                COUNT(*) AS total
            FROM quest_occurrence
            WHERE user_id = :userId
              AND DATE(period_key) BETWEEN :from AND :to
            GROUP BY DATE(period_key)
            ORDER BY d
            """, nativeQuery = true)
    List<Object[]> countDaily(@Param("userId") Long userId,
                              @Param("from") LocalDate from,
                              @Param("to") LocalDate to);

    // 주 단위 집계 (ISO 주차, 월요일 시작)
    @Query(value = """
            SELECT 
                YEARWEEK(period_key, 1) AS week_key,
                SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed,
                COUNT(*) AS total
            FROM quest_occurrence
            WHERE user_id = :userId
              AND DATE(period_key) BETWEEN :from AND :to
            GROUP BY YEARWEEK(period_key, 1)
            ORDER BY week_key
            """, nativeQuery = true)
    List<Object[]> countWeekly(@Param("userId") Long userId,
                               @Param("from") LocalDate from,
                               @Param("to") LocalDate to);

    // 월 단위 집계 ("YYYY-MM")
    @Query(value = """
            SELECT 
                DATE_FORMAT(period_key, '%Y-%m') AS month_key,
                SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed,
                COUNT(*) AS total
            FROM quest_occurrence
            WHERE user_id = :userId
              AND DATE(period_key) BETWEEN :from AND :to
            GROUP BY DATE_FORMAT(period_key, '%Y-%m')
            ORDER BY month_key
            """, nativeQuery = true)
    List<Object[]> countMonthly(@Param("userId") Long userId,
                                @Param("from") LocalDate from,
                                @Param("to") LocalDate to);

    // 연 단위 집계 (정수 연도)
    @Query(value = """
            SELECT 
                YEAR(period_key) AS year_key,
                SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed,
                COUNT(*) AS total
            FROM quest_occurrence
            WHERE user_id = :userId
              AND DATE(period_key) BETWEEN :from AND :to
            GROUP BY YEAR(period_key)
            ORDER BY year_key
            """, nativeQuery = true)
    List<Object[]> countYearly(@Param("userId") Long userId,
                               @Param("from") LocalDate from,
                               @Param("to") LocalDate to);

    // 범위 내 원본 행 조회 (서비스에서 추가 가공 시 사용)
    List<QuestOccurrence> findAllByUserIdAndPeriodKeyBetweenOrderByPeriodKeyAsc(
            Long userId, LocalDate from, LocalDate to
    );

    // 상태별 카운트 (분석용)
    long countByUserIdAndStatusAndPeriodKeyBetween(
            Long userId, String status, LocalDate from, LocalDate to
    );

    // 기간 키별 그룹 카운트 (JPQL Projection 예시)
    @Query("""
           select q.periodKey as periodKey, count(q) as cnt
           from QuestOccurrence q
           where q.userId = :userId
             and q.status = :status
             and q.periodKey between :from and :to
           group by q.periodKey
           order by q.periodKey asc
           """)
    List<PeriodCount> countGroupedByPeriodKey(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    interface PeriodCount {
        LocalDate getPeriodKey();
        long getCnt();
    }
}