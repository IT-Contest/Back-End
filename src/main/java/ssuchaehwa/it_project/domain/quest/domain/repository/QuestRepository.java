package ssuchaehwa.it_project.domain.quest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssuchaehwa.it_project.domain.quest.domain.entity.Quest;

import java.util.List;

public interface QuestRepository extends JpaRepository<Quest, Long> {

    @Query("SELECT q FROM Quest q WHERE q.user.id = :userId")
    List<Quest> findAllByUserId(@Param("userId") Long userId);

}
