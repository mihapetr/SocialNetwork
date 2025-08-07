package com.mihapetr.socialnetwork.domain;

import static com.mihapetr.socialnetwork.domain.ChatTestSamples.*;
import static com.mihapetr.socialnetwork.domain.CommentTestSamples.*;
import static com.mihapetr.socialnetwork.domain.PostTestSamples.*;
import static com.mihapetr.socialnetwork.domain.ProfileTestSamples.*;
import static com.mihapetr.socialnetwork.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mihapetr.socialnetwork.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Profile.class);
        Profile profile1 = getProfileSample1();
        Profile profile2 = new Profile();
        assertThat(profile1).isNotEqualTo(profile2);

        profile2.setId(profile1.getId());
        assertThat(profile1).isEqualTo(profile2);

        profile2 = getProfileSample2();
        assertThat(profile1).isNotEqualTo(profile2);
    }

    @Test
    void postTest() {
        Profile profile = getProfileRandomSampleGenerator();
        Post postBack = getPostRandomSampleGenerator();

        profile.addPost(postBack);
        assertThat(profile.getPosts()).containsOnly(postBack);
        assertThat(postBack.getProfile()).isEqualTo(profile);

        profile.removePost(postBack);
        assertThat(profile.getPosts()).doesNotContain(postBack);
        assertThat(postBack.getProfile()).isNull();

        profile.posts(new HashSet<>(Set.of(postBack)));
        assertThat(profile.getPosts()).containsOnly(postBack);
        assertThat(postBack.getProfile()).isEqualTo(profile);

        profile.setPosts(new HashSet<>());
        assertThat(profile.getPosts()).doesNotContain(postBack);
        assertThat(postBack.getProfile()).isNull();
    }

    @Test
    void commentTest() {
        Profile profile = getProfileRandomSampleGenerator();
        Comment commentBack = getCommentRandomSampleGenerator();

        profile.addComment(commentBack);
        assertThat(profile.getComments()).containsOnly(commentBack);
        assertThat(commentBack.getProfile()).isEqualTo(profile);

        profile.removeComment(commentBack);
        assertThat(profile.getComments()).doesNotContain(commentBack);
        assertThat(commentBack.getProfile()).isNull();

        profile.comments(new HashSet<>(Set.of(commentBack)));
        assertThat(profile.getComments()).containsOnly(commentBack);
        assertThat(commentBack.getProfile()).isEqualTo(profile);

        profile.setComments(new HashSet<>());
        assertThat(profile.getComments()).doesNotContain(commentBack);
        assertThat(commentBack.getProfile()).isNull();
    }

    @Test
    void otherTest() {
        Profile profile = getProfileRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        profile.addOther(profileBack);
        assertThat(profile.getOthers()).containsOnly(profileBack);

        profile.removeOther(profileBack);
        assertThat(profile.getOthers()).doesNotContain(profileBack);

        profile.others(new HashSet<>(Set.of(profileBack)));
        assertThat(profile.getOthers()).containsOnly(profileBack);

        profile.setOthers(new HashSet<>());
        assertThat(profile.getOthers()).doesNotContain(profileBack);
    }

    @Test
    void chatTest() {
        Profile profile = getProfileRandomSampleGenerator();
        Chat chatBack = getChatRandomSampleGenerator();

        profile.addChat(chatBack);
        assertThat(profile.getChats()).containsOnly(chatBack);

        profile.removeChat(chatBack);
        assertThat(profile.getChats()).doesNotContain(chatBack);

        profile.chats(new HashSet<>(Set.of(chatBack)));
        assertThat(profile.getChats()).containsOnly(chatBack);

        profile.setChats(new HashSet<>());
        assertThat(profile.getChats()).doesNotContain(chatBack);
    }

    @Test
    void profileTest() {
        Profile profile = getProfileRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        profile.addProfile(profileBack);
        assertThat(profile.getProfiles()).containsOnly(profileBack);
        assertThat(profileBack.getOthers()).containsOnly(profile);

        profile.removeProfile(profileBack);
        assertThat(profile.getProfiles()).doesNotContain(profileBack);
        assertThat(profileBack.getOthers()).doesNotContain(profile);

        profile.profiles(new HashSet<>(Set.of(profileBack)));
        assertThat(profile.getProfiles()).containsOnly(profileBack);
        assertThat(profileBack.getOthers()).containsOnly(profile);

        profile.setProfiles(new HashSet<>());
        assertThat(profile.getProfiles()).doesNotContain(profileBack);
        assertThat(profileBack.getOthers()).doesNotContain(profile);
    }
}
