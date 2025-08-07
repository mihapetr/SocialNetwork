package com.mihapetr.socialnetwork.repository;

import com.mihapetr.socialnetwork.domain.Profile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ProfileRepositoryWithBagRelationshipsImpl implements ProfileRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String PROFILES_PARAMETER = "profiles";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Profile> fetchBagRelationships(Optional<Profile> profile) {
        return profile.map(this::fetchOthers).map(this::fetchChats);
    }

    @Override
    public Page<Profile> fetchBagRelationships(Page<Profile> profiles) {
        return new PageImpl<>(fetchBagRelationships(profiles.getContent()), profiles.getPageable(), profiles.getTotalElements());
    }

    @Override
    public List<Profile> fetchBagRelationships(List<Profile> profiles) {
        return Optional.of(profiles).map(this::fetchOthers).map(this::fetchChats).orElse(Collections.emptyList());
    }

    Profile fetchOthers(Profile result) {
        return entityManager
            .createQuery("select profile from Profile profile left join fetch profile.others where profile.id = :id", Profile.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Profile> fetchOthers(List<Profile> profiles) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, profiles.size()).forEach(index -> order.put(profiles.get(index).getId(), index));
        List<Profile> result = entityManager
            .createQuery("select profile from Profile profile left join fetch profile.others where profile in :profiles", Profile.class)
            .setParameter(PROFILES_PARAMETER, profiles)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Profile fetchChats(Profile result) {
        return entityManager
            .createQuery("select profile from Profile profile left join fetch profile.chats where profile.id = :id", Profile.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Profile> fetchChats(List<Profile> profiles) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, profiles.size()).forEach(index -> order.put(profiles.get(index).getId(), index));
        List<Profile> result = entityManager
            .createQuery("select profile from Profile profile left join fetch profile.chats where profile in :profiles", Profile.class)
            .setParameter(PROFILES_PARAMETER, profiles)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
