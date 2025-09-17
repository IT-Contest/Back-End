package ssuchaehwa.it_project.domain.quest.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssuchaehwa.it_project.domain.model.enums.FriendStatus;
import ssuchaehwa.it_project.domain.quest.domain.entity.InvitedFriend;
import ssuchaehwa.it_project.domain.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvitedFriendRepository extends JpaRepository<InvitedFriend, Long> {

    @Query("""
        SELECT i FROM InvitedFriend i
        WHERE (i.fromUser.id = :userId OR i.toUser.id = :userId)
          AND i.status = 'ACCEPTED'
    """)
    List<InvitedFriend> findAcceptedFriends(@Param("userId") Long userId);

    void deleteByFromUserOrToUser(User fromUser, User toUser);

    Optional<InvitedFriend> findByToken(String token);

    boolean existsByFromUserAndToUserAndStatus(User fromUser, User toUser, FriendStatus status);

    boolean existsByFromUserAndToUser(User fromUser, User toUser);

    @Modifying
    @Query("DELETE FROM InvitedFriend i WHERE i.status = 'PENDING' AND i.toUser IS NULL AND i.expiresAt < :now")
    void deleteExpiredPendingInvites(@Param("now") LocalDateTime now);
}
