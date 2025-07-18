package ssuchaehwa.it_project.domain.quest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssuchaehwa.it_project.domain.quest.domain.entity.InvitedFriend;

import java.util.List;

public interface InvitedFriendRepository extends JpaRepository<InvitedFriend, Long> {

    @Query("""
        SELECT i FROM InvitedFriend i
        WHERE (i.fromUser.id = :userId OR i.toUser.id = :userId)
          AND i.status = 'ACCEPTED'
    """)
    List<InvitedFriend> findAcceptedFriends(@Param("userId") Long userId);
}
