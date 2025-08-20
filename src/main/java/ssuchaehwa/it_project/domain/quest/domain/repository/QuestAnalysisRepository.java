package ssuchaehwa.it_project.domain.quest.domain.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ssuchaehwa.it_project.domain.quest.domain.entity.QuestOccurrence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface QuestAnalysisRepository extends JpaRepository<QuestOccurrence, Long> {

    // 일 단위 완료/전체 집계
    @Query(value = "SELECT DATE(period_key) as d, " +
            "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed, " +
            "COUNT(*) as total " +
            "FROM quest_occurrence " +
            "WHERE user_id = :userId AND period_key BETWEEN :from AND :to " +
            "GROUP BY DATE(period_key) " +
            "ORDER BY d", nativeQuery = true)
    List<Object[]> countDaily(@Param("userId") Long userId,
                              @Param("from") LocalDate from,
                              @Param("to") LocalDate to);

    // 주 단위 집계
    @Query(value = "SELECT YEARWEEK(period_key, 1) as w, " +
            "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed, " +
            "COUNT(*) as total " +
            "FROM quest_occurrence " +
            "WHERE user_id = :userId AND period_key BETWEEN :from AND :to " +
            "GROUP BY YEARWEEK(period_key, 1) " +
            "ORDER BY w", nativeQuery = true)
    List<Object[]> countWeekly(@Param("userId") Long userId,
                               @Param("from") LocalDate from,
                               @Param("to") LocalDate to);

    // 범위 조회 (필요시 서비스에서 직접 합산)
    List<QuestOccurrence> findAllByUserIdAndPeriodKeyBetweenOrderByPeriodKeyAsc(
            Long userId, LocalDate from, LocalDate to
    );

    // 집계(분석용) — 상태별 카운트
    long countByUserIdAndStatusAndPeriodKeyBetween(
            Long userId, String status, LocalDate from, LocalDate to
    );

    // 버킷별(group by periodKey) 카운트 — 일/주/월/연 버킷에 맞춰 한 번에 가져올 때 사용
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

    // Projection
    interface PeriodCount {
        LocalDate getPeriodKey();
        long getCnt();
    }

    // 월 단위 집계 ("YYYY-MM" 형태로 그룹핑)
    @Query(value = "SELECT DATE_FORMAT(period_key, '%Y-%m') as ym, " +
            "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed, " +
            "COUNT(*) as total " +
            "FROM quest_occurrence " +
            "WHERE user_id = :userId AND period_key BETWEEN :from AND :to " +
            "GROUP BY DATE_FORMAT(period_key, '%Y-%m') " +
            "ORDER BY ym", nativeQuery = true)
    List<Object[]> countMonthly(@Param("userId") Long userId,
                                @Param("from") LocalDate from,
                                @Param("to") LocalDate to);

    // 연 단위 집계 ("YYYY" 정수 연도 키)
    @Query(value = "SELECT YEAR(period_key) as y, " +
            "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed, " +
            "COUNT(*) as total " +
            "FROM quest_occurrence " +
            "WHERE user_id = :userId AND period_key BETWEEN :from AND :to " +
            "GROUP BY YEAR(period_key) " +
            "ORDER BY y", nativeQuery = true)
    List<Object[]> countYearly(@Param("userId") Long userId,
                               @Param("from") LocalDate from,
                               @Param("to") LocalDate to);
}