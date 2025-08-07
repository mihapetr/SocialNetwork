package com.mihapetr.socialnetwork.domain;

import static com.mihapetr.socialnetwork.domain.ChatTestSamples.*;
import static com.mihapetr.socialnetwork.domain.MessageTestSamples.*;
import static com.mihapetr.socialnetwork.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mihapetr.socialnetwork.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ChatTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Chat.class);
        Chat chat1 = getChatSample1();
        Chat chat2 = new Chat();
        assertThat(chat1).isNotEqualTo(chat2);

        chat2.setId(chat1.getId());
        assertThat(chat1).isEqualTo(chat2);

        chat2 = getChatSample2();
        assertThat(chat1).isNotEqualTo(chat2);
    }

    @Test
    void chatTest() {
        Chat chat = getChatRandomSampleGenerator();
        Message messageBack = getMessageRandomSampleGenerator();

        chat.addChat(messageBack);
        assertThat(chat.getChats()).containsOnly(messageBack);
        assertThat(messageBack.getChat()).isEqualTo(chat);

        chat.removeChat(messageBack);
        assertThat(chat.getChats()).doesNotContain(messageBack);
        assertThat(messageBack.getChat()).isNull();

        chat.chats(new HashSet<>(Set.of(messageBack)));
        assertThat(chat.getChats()).containsOnly(messageBack);
        assertThat(messageBack.getChat()).isEqualTo(chat);

        chat.setChats(new HashSet<>());
        assertThat(chat.getChats()).doesNotContain(messageBack);
        assertThat(messageBack.getChat()).isNull();
    }

    @Test
    void profileTest() {
        Chat chat = getChatRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        chat.addProfile(profileBack);
        assertThat(chat.getProfiles()).containsOnly(profileBack);
        assertThat(profileBack.getChats()).containsOnly(chat);

        chat.removeProfile(profileBack);
        assertThat(chat.getProfiles()).doesNotContain(profileBack);
        assertThat(profileBack.getChats()).doesNotContain(chat);

        chat.profiles(new HashSet<>(Set.of(profileBack)));
        assertThat(chat.getProfiles()).containsOnly(profileBack);
        assertThat(profileBack.getChats()).containsOnly(chat);

        chat.setProfiles(new HashSet<>());
        assertThat(chat.getProfiles()).doesNotContain(profileBack);
        assertThat(profileBack.getChats()).doesNotContain(chat);
    }
}
