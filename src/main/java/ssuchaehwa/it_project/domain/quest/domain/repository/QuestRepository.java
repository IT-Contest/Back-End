package ssuchaehwa.it_project.domain.quest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssuchaehwa.it_project.domain.quest.domain.entity.Quest;
import ssuchaehwa.it_project.domain.user.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface QuestRepository extends JpaRepository<Quest, Long> {

    @Query("SELECT q FROM Quest q WHERE q.user.id = :userId")
    List<Quest> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT q FROM Quest q WHERE q.user.id = :userId AND (q.dueDate IS NULL OR q.dueDate >= CURRENT_DATE)")
    List<Quest> findActiveQuestsByUserId(@Param("userId") Long userId);

    void deleteByUser(User user);

    Optional<Quest> findByUserIdAndTitle(Long userId, String title);
}
