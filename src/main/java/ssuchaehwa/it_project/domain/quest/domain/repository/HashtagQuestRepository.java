package ssuchaehwa.it_project.domain.quest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssuchaehwa.it_project.domain.quest.domain.entity.HashtagQuest;
import ssuchaehwa.it_project.domain.quest.domain.entity.Quest;

import java.util.List;

public interface HashtagQuestRepository extends JpaRepository<HashtagQuest, Long> {
    void deleteByQuest(Quest quest);
    List<HashtagQuest> findByQuest(Quest quest);
}
