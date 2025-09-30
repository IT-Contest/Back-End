package ssuchaehwa.it_project.domain.quest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssuchaehwa.it_project.domain.quest.domain.entity.QuestOccurrence;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import ssuchaehwa.it_project.domain.quest.domain.enums.QuestSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QuestOccurrenceRepository extends JpaRepository<QuestOccurrence, Long> {

    /* ===== 단건 조회/존재 확인 (완료 처리 upsert 시 사용) ===== */

    // ✅ questSource 포함하도록 수정
    Optional<QuestOccurrence> findByTemplateIdAndPeriodKeyAndQuestSource(
            Long templateId,
            LocalDate periodKey,
            QuestSource questSource
    );

    boolean existsByTemplateIdAndPeriodKeyAndQuestSource(
            Long templateId,
            LocalDate periodKey,
            QuestSource questSource
    );

    /* ===== 범위 조회 (오늘 할 일/리스트용) ===== */

    List<QuestOccurrence> findAllByUserIdAndPeriodKeyBetweenOrderByPeriodKeyAsc(
            Long userId,
            LocalDate from,
            LocalDate to
    );

    /* ===== 집계(분석용) — 상태별 카운트 ===== */

    long countByUserIdAndStatusAndPeriodKeyBetween(
            Long userId,
            String status,
            LocalDate from,
            LocalDate to
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

    /* ===== 삭제 관련 ===== */

    @Transactional
    @Modifying
    void deleteAllByTemplateId(Long templateId);

    @Transactional(readOnly = true)
    List<QuestOccurrence> findAllByTemplateId(Long templateId);

    void deleteAllByTemplateIdAndQuestSource(Long templateId, QuestSource questSource);

    /* ===== 상태 업데이트 (questSource 포함) ===== */

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
           UPDATE QuestOccurrence q
           SET q.status = :status, q.completedAt = :completedAt
           WHERE q.templateId = :templateId
             AND q.periodKey = :periodKey
             AND q.questSource = :questSource
           """)
    int updateStatusByTemplateIdAndPeriodKeyAndQuestSource(
            @Param("templateId") Long templateId,
            @Param("periodKey") LocalDate periodKey,
            @Param("questSource") QuestSource questSource,
            @Param("status") String status,
            @Param("completedAt") java.time.LocalDateTime completedAt
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
           UPDATE QuestOccurrence q
           SET q.status = :status
           WHERE q.templateId = :templateId
             AND q.periodKey = :periodKey
             AND q.questSource = :questSource
           """)
    int updateStatusOnlyByTemplateIdAndPeriodKeyAndQuestSource(
            @Param("templateId") Long templateId,
            @Param("periodKey") LocalDate periodKey,
            @Param("questSource") QuestSource questSource,
            @Param("status") String status
    );

    @Modifying
    @Query("DELETE FROM QuestOccurrence qo WHERE qo.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
