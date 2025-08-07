package com.mihapetr.socialnetwork.domain;

import static com.mihapetr.socialnetwork.domain.ChatTestSamples.*;
import static com.mihapetr.socialnetwork.domain.CommentTestSamples.*;
import static com.mihapetr.socialnetwork.domain.MessageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mihapetr.socialnetwork.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Message.class);
        Message message1 = getMessageSample1();
        Message message2 = new Message();
        assertThat(message1).isNotEqualTo(message2);

        message2.setId(message1.getId());
        assertThat(message1).isEqualTo(message2);

        message2 = getMessageSample2();
        assertThat(message1).isNotEqualTo(message2);
    }

    @Test
    void commentTest() {
        Message message = getMessageRandomSampleGenerator();
        Comment commentBack = getCommentRandomSampleGenerator();

        message.setComment(commentBack);
        assertThat(message.getComment()).isEqualTo(commentBack);
        assertThat(commentBack.getParent()).isEqualTo(message);

        message.comment(null);
        assertThat(message.getComment()).isNull();
        assertThat(commentBack.getParent()).isNull();
    }

    @Test
    void chatTest() {
        Message message = getMessageRandomSampleGenerator();
        Chat chatBack = getChatRandomSampleGenerator();

        message.setChat(chatBack);
        assertThat(message.getChat()).isEqualTo(chatBack);

        message.chat(null);
        assertThat(message.getChat()).isNull();
    }
}
