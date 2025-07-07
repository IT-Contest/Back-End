package ssuchaehwa.it_project.domain.quest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssuchaehwa.it_project.domain.quest.domain.entity.Quest;

public interface QuestRepository extends JpaRepository<Quest, Long> {
}
