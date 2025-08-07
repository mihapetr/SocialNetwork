package com.mihapetr.socialnetwork.repository;

import com.mihapetr.socialnetwork.domain.Message;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Message entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("select message from Message message where message.user.login = ?#{authentication.name}")
    List<Message> findByUserIsCurrentUser();
}
