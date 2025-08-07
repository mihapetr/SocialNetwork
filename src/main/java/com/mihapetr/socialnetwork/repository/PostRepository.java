package com.mihapetr.socialnetwork.repository;

import com.mihapetr.socialnetwork.domain.Post;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Post entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select post from Post post where post.user.login = ?#{authentication.name}")
    List<Post> findByUserIsCurrentUser();
}
