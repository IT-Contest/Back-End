package ssuchaehwa.it_project.domain.pomodoro.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssuchaehwa.it_project.domain.pomodoro.domain.entity.Pomodoro;

import java.time.LocalDateTime;
import java.util.List;

public interface PomodoroRepository extends JpaRepository<Pomodoro, Long> {

    // 범위 조회(필요시 서비스에서 직접 합산)
    List<Pomodoro> findAllByUser_IdAndStartTimeBetween(Long userId, LocalDateTime from, LocalDateTime to);

    // 일 단위 집계 (KST 범위는 서비스에서 from/to를 KST 기준으로 전달)
    @Query(value = "SELECT DATE(start_time) AS d, COUNT(*) AS cnt " +
                   "FROM FocusSession " +
                   "WHERE user_id = :userId AND start_time BETWEEN :from AND :to " +
                   "GROUP BY DATE(start_time) " +
                   "ORDER BY d", nativeQuery = true)
    List<Object[]> countByDay(@Param("userId") Long userId,
                              @Param("from") LocalDateTime from,
                              @Param("to") LocalDateTime to);
}