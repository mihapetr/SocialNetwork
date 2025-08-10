package com.mihapetr.socialnetwork.repository;

import com.mihapetr.socialnetwork.NotGenerated;
import com.mihapetr.socialnetwork.domain.Chat;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Chat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("select chat from Chat chat where chat.user.login = ?#{authentication.name}")
    List<Chat> findByUserIsCurrentUser();

    @NotGenerated
    @Query("select c from Chat c join c.profiles p where p.user.login = :login")
    List<Chat> findAllByCurrentProfile(@Param("login") String login);
}
