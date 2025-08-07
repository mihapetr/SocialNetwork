package com.mihapetr.socialnetwork.repository;

import com.mihapetr.socialnetwork.domain.Chat;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Chat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("select chat from Chat chat where chat.user.login = ?#{authentication.name}")
    List<Chat> findByUserIsCurrentUser();
}
