package com.mihapetr.socialnetwork.repository;

import com.mihapetr.socialnetwork.NotGenerated;
import com.mihapetr.socialnetwork.domain.Profile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Profile entity.
 *
 * When extending this class, extend ProfileRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ProfileRepository extends ProfileRepositoryWithBagRelationships, JpaRepository<Profile, Long> {
    @Query("select profile from Profile profile where profile.user.login = ?#{authentication.name}")
    List<Profile> findByUserIsCurrentUser();

    default Optional<Profile> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    @NotGenerated
    @Query("select profile from Profile profile where profile.user.login = :login")
    Optional<Profile> findByUserLogin(@Param("login") String login);

    default List<Profile> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Profile> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
