package ssuchaehwa.it_project.domain.analysis.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssuchaehwa.it_project.domain.analysis.domain.entity.CoachingRecord;
import ssuchaehwa.it_project.domain.user.domain.entity.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CoachingRecordRepository extends JpaRepository<CoachingRecord, Long> {

    // 사용자별 코칭 기록 조회 (최신순)
    List<CoachingRecord> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 사용자별 코칭 기록 조회 (페이지네이션)
    List<CoachingRecord> findByUserIdOrderByCreatedAtDesc(Long userId, org.springframework.data.domain.Pageable pageable);

    // 사용자별 특정 날짜에 코칭 기록이 있는지 확인
    boolean existsByUserIdAndAnalysisDate(Long userId, LocalDate analysisDate);

    void deleteByUser(User user);

}
