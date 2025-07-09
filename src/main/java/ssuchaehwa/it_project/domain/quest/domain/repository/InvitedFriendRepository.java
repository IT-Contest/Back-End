package ssuchaehwa.it_project.domain.quest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssuchaehwa.it_project.domain.quest.domain.entity.InvitedFriend;

import java.util.List;

public interface InvitedFriendRepository extends JpaRepository<InvitedFriend, Long> {

    @Query("SELECT i FROM InvitedFriend i WHERE i.quest.id = :questId")
    List<InvitedFriend> findAllByQuestId(@Param("questId") Long questId);

}
