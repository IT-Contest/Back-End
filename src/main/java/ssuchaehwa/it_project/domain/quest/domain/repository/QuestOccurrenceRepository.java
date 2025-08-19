package ssuchaehwa.it_project.domain.quest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssuchaehwa.it_project.domain.quest.domain.entity.QuestOccurrence;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QuestOccurrenceRepository extends JpaRepository<QuestOccurrence, Long> {

    /* ===== 단건 조회/존재 확인 (완료 처리 upsert 시 사용) ===== */

    Optional<QuestOccurrence> findByTemplateIdAndPeriodKey(Long templateId, LocalDate periodKey);

    boolean existsByTemplateIdAndPeriodKey(Long templateId, LocalDate periodKey);


    /* ===== 범위 조회 (오늘 할 일/리스트용) ===== */

    List<QuestOccurrence> findAllByUserIdAndPeriodKeyBetweenOrderByPeriodKeyAsc(
            Long userId, LocalDate from, LocalDate to
    );


    /* ===== 집계(분석용) — 상태별 카운트 ===== */

    long countByUserIdAndStatusAndPeriodKeyBetween(
            Long userId, String status, LocalDate from, LocalDate to
    );

    /* 버킷별(group by periodKey) 카운트 — 일/주/월/연 버킷에 맞춰 한 번에 가져올 때 사용 */
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

    /* ===== Projection ===== */
    interface PeriodCount {
        LocalDate getPeriodKey();
        long getCnt();
    }

    //
    @Transactional
    @Modifying
    void deleteAllByTemplateId(Long templateId);

    @Transactional(readOnly = true)
    List<QuestOccurrence> findAllByTemplateId(Long templateId);
}