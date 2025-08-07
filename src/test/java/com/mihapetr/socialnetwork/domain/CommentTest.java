package com.mihapetr.socialnetwork.domain;

import static com.mihapetr.socialnetwork.domain.CommentTestSamples.*;
import static com.mihapetr.socialnetwork.domain.MessageTestSamples.*;
import static com.mihapetr.socialnetwork.domain.PostTestSamples.*;
import static com.mihapetr.socialnetwork.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mihapetr.socialnetwork.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Comment.class);
        Comment comment1 = getCommentSample1();
        Comment comment2 = new Comment();
        assertThat(comment1).isNotEqualTo(comment2);

        comment2.setId(comment1.getId());
        assertThat(comment1).isEqualTo(comment2);

        comment2 = getCommentSample2();
        assertThat(comment1).isNotEqualTo(comment2);
    }

    @Test
    void parentTest() {
        Comment comment = getCommentRandomSampleGenerator();
        Message messageBack = getMessageRandomSampleGenerator();

        comment.setParent(messageBack);
        assertThat(comment.getParent()).isEqualTo(messageBack);

        comment.parent(null);
        assertThat(comment.getParent()).isNull();
    }

    @Test
    void postTest() {
        Comment comment = getCommentRandomSampleGenerator();
        Post postBack = getPostRandomSampleGenerator();

        comment.setPost(postBack);
        assertThat(comment.getPost()).isEqualTo(postBack);

        comment.post(null);
        assertThat(comment.getPost()).isNull();
    }

    @Test
    void profileTest() {
        Comment comment = getCommentRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        comment.setProfile(profileBack);
        assertThat(comment.getProfile()).isEqualTo(profileBack);

        comment.profile(null);
        assertThat(comment.getProfile()).isNull();
    }
}
