package com.mihapetr.socialnetwork.cucumber.stepdefs;

import com.mihapetr.socialnetwork.domain.Chat;
import com.mihapetr.socialnetwork.domain.Message;
import com.mihapetr.socialnetwork.domain.Profile;
import com.mihapetr.socialnetwork.domain.User;
import com.mihapetr.socialnetwork.repository.ChatRepository;
import com.mihapetr.socialnetwork.repository.MessageRepository;
import com.mihapetr.socialnetwork.repository.ProfileRepository;
import com.mihapetr.socialnetwork.service.dto.AdminUserDTO;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public class Chat_with_Friends extends Common {

    static Long requestChatId, friendChatId;
    static String chatFriendLogin;
    String token2;
    Profile requestProfile, acceptProfile, chatProfile;

    @Autowired
    ChatRepository chatRepository;

    @Autowired
    MessageRepository messageRepository;

    static boolean done = false;

    void createChatRequestForUser() {
        Message requestMessage = new Message().content("I want to chat!");
        requestMessage = messageRepository.save(requestMessage);
        Chat chat = new Chat()
            .initiatorName("seconduser")
            .accepted(false)
            .addChat(requestMessage)
            .addProfile(mainProfile)
            .addProfile(requestProfile);
        requestChatId = chatRepository.save(chat).getId();
    }

    void createFriendForChat() {
        Message requestMessage = new Message().content("I want to chat!");
        requestMessage = messageRepository.save(requestMessage);
        Chat chat = new Chat()
            .initiatorName("thirduser")
            .accepted(true)
            .addChat(requestMessage)
            .addProfile(mainProfile)
            .addProfile(chatProfile);
        chatFriendLogin = "thirduser";
        friendChatId = chatRepository.save(chat).getId();
        // make them friends
        mainProfile.addOther(chatProfile);
        profileRepository.save(mainProfile);
    }

    @Before
    public void before() {
        user = userRepository.findOneByLogin(existingLogin).orElse(null);
        if (done) return; // DB needs to be set up only once per Cucumber feature test run

        setup();
        user = userRepository.findOneByLogin(existingLogin).orElse(null);
        // requesting friendship scenario
        requestProfile = createUserProfile("firstuser"); // main user will request friendship with this other profile
        // accepting friendship scenario
        acceptProfile = createUserProfile("seconduser"); // main user will accept friend request from this other profile
        chatProfile = createUserProfile("thirduser"); // main user will send a message to this friend
        createChatRequestForUser();
        // sending a message scenario
        createFriendForChat();

        done = true;
    }

    @Given("user is logged in")
    public void user_is_logged_in() {
        token = signIn(existingLogin, existingPassword);
        assert token != null : "Sign in token is null";
    }

    List<Profile> profilesResponse;

    @Given("user navigates to Profiles view")
    public void user_navigates_to_Profiles_view() {
        profilesResponse = restClient
            .get()
            .uri("http://localhost:" + port + "/api/profiles")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<Profile>>() {})
            .getBody();
        assert profilesResponse != null : "Profiles response is null";
    }

    Long selectedProfileId;

    @And("user selects Profile details")
    public void user_selects_Profile_details() {
        selectedProfileId = profilesResponse
            .stream()
            .filter(profile -> profile.getUser().getLogin().equals("firstuser"))
            .findFirst()
            .orElse(null)
            .getId();
        assert selectedProfileId != null : "selected Profile id is null";
    }

    @And("user is not other profile's friend already")
    public void user_is_not_other_profile_s_friend_already() {
        Profile selectedProfile = restClient
            .get()
            .uri("http://localhost:" + port + "/api/profiles/" + selectedProfileId)
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(Profile.class)
            .getBody();
        Profile userProfile = restClient
            .get()
            .uri("http://localhost:" + port + "/api/profiles/current-user")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(Profile.class)
            .getBody();
        Set<Profile> others = selectedProfile.getOthers();
        Set<Profile> profiles = selectedProfile.getProfiles();

        assert others == null || !others.contains(userProfile) : "other profile friends with user";
        assert profiles == null || !profiles.contains(userProfile) : "other profile friends with user";
    }

    Chat friendRequestResponse;

    @When("user selects request friendhip option")
    public void user_selects_request_friendhip_option() {
        friendRequestResponse = restClient
            .post()
            .uri("http://localhost:" + port + "/api/chats/request-chat-with-profile/" + selectedProfileId)
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(Chat.class)
            .getBody();
    }

    @Then("chat with request is created with the other profile")
    public void chat_with_request_is_created_with_the_other_profile() {
        Chat dbChat = chatRepository.findById(friendRequestResponse.getId()).orElse(null);
        assert dbChat != null : "dbChat is null";
        assert dbChat.getInitiatorName().equals(existingLogin) : "user who requested chat/friends unexpected; initiatorName = " +
        dbChat.getInitiatorName();
        assert !dbChat.getAccepted() : "chat accepted without other user's approval";
    }

    List<Chat> userChats;

    @Given("user navigates to Chats view")
    public void user_navigates_to_Chats_view() {
        userChats = restClient
            .get()
            .uri("http://localhost:" + port + "/api/chats")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<Chat>>() {})
            .getBody();
    }

    @And("user has a friend request message from other profile")
    public void user_has_a_friend_request_message_from_other_profile() {
        assert userChats
            .stream()
            .anyMatch(chat -> chat.getInitiatorName().equals("seconduser")
            ) : "User does not have a friend request message from other profile; userChats = " + userChats;
    }

    Chat chatWithRequest;

    @And("user opens the chat with request")
    public void user_opens_the_chat_with_request() {
        chatWithRequest = restClient
            .get()
            .uri("http://localhost:" + port + "/api/chats/" + requestChatId)
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(Chat.class)
            .getBody();
    }

    Profile updatedProfile;

    @When("user selects accept option")
    public void user_selects_accept_option() {
        updatedProfile = restClient
            .patch()
            .uri("http://localhost:" + port + "/api/chats/" + requestChatId + "/accept")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(Profile.class)
            .getBody();
    }

    @Then("user and other profile are friends")
    public void user_and_other_profile_are_friends() {
        String initiatorLogin = chatWithRequest.getInitiatorName();
        Profile userProfile = restClient
            .get()
            .uri("http://localhost:" + port + "/api/profiles/current-user")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(Profile.class)
            .getBody();
        Set<Profile> others = userProfile.getOthers();
        Set<Profile> profiles = userProfile.getProfiles();

        boolean inOthers = false, inProfiles = false;
        if (others != null) inOthers = others.stream().anyMatch(profile -> profile.getUser().getLogin().equals(initiatorLogin));
        if (profiles != null) inProfiles = profiles.stream().anyMatch(profile -> profile.getUser().getLogin().equals(initiatorLogin));
        assert inOthers || inProfiles : "No initiator in user's others nor profiles; initiatorLogin = " +
        initiatorLogin +
        ", others = " +
        others +
        ", profiles = " +
        profiles;
    }

    Chat chatWithOther;

    @And("user opens a Chat with a recipient")
    public void user_opens_a_Chat_with_a_recipient() {
        chatWithOther = restClient
            .get()
            .uri("http://localhost:" + port + "/api/chats/" + friendChatId)
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(Chat.class)
            .getBody();
    }

    @And("user is recipient's friend")
    public void user_is_recipient_s_friend() {
        Profile userProfile = restClient
            .get()
            .uri("http://localhost:" + port + "/api/profiles/current-user")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(Profile.class)
            .getBody();
        Set<Profile> others = userProfile.getOthers();
        Set<Profile> profiles = userProfile.getProfiles();

        assert others.stream().anyMatch(profile -> profile.getUser().getLogin().equals(chatFriendLogin)) ||
        profiles
            .stream()
            .anyMatch(profile -> profile.getUser().getLogin().equals(chatFriendLogin)) : "user is not recipient's friend; others = " +
        userProfile.getOthers() +
        "; profiles = " +
        userProfile.getProfiles();
    }

    Chat chatUserView, chatFriendView;
    String content = "Hi, I am sending you a message!";

    @When("user submits Message content")
    public void user_submits_Message_content() {
        Message newMessage = new Message().content(content);
        newMessage = restClient
            .patch()
            .uri("http://localhost:" + port + "/api/chats/" + chatWithOther.getId() + "/message")
            .header("Authorization", "Bearer " + token)
            .body(newMessage)
            .retrieve()
            .toEntity(Message.class)
            .getBody();
    }

    @Then("message is created in the chat")
    public void message_is_created_in_the_chat() {
        chatUserView = restClient
            .get()
            .uri("http://localhost:" + port + "/api/chats/" + chatWithOther.getId())
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .toEntity(Chat.class)
            .getBody();

        token2 = signIn("seconduser", "seconduserpass");
        chatFriendView = restClient
            .get()
            .uri("http://localhost:" + port + "/api/chats/" + chatWithOther.getId())
            .header("Authorization", "Bearer " + token2)
            .retrieve()
            .toEntity(Chat.class)
            .getBody();
    }

    @And("both users can see the message")
    public void both_users_can_see_the_message() {
        assert chatUserView != null &&
        chatUserView
            .getChats()
            .stream()
            .anyMatch(chat -> chat.getContent().equals(content)
            ) : "chat from user's perspective is null or does not contain the sent message";
        assert chatFriendView != null &&
        chatUserView
            .getChats()
            .stream()
            .anyMatch(chat -> chat.getContent().equals(content)
            ) : "chat from friend's perspective is null or does not contain the sent message";
    }
}
