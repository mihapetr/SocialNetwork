package com.mihapetr.socialnetwork.web.rest;

import com.mihapetr.socialnetwork.NotGenerated;
import com.mihapetr.socialnetwork.domain.Chat;
import com.mihapetr.socialnetwork.domain.Message;
import com.mihapetr.socialnetwork.domain.Profile;
import com.mihapetr.socialnetwork.repository.ChatRepository;
import com.mihapetr.socialnetwork.repository.ProfileRepository;
import com.mihapetr.socialnetwork.security.SecurityUtils;
import com.mihapetr.socialnetwork.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mihapetr.socialnetwork.domain.Chat}.
 */
@RestController
@RequestMapping("/api/chats")
@Transactional
public class ChatResource {

    private static final Logger LOG = LoggerFactory.getLogger(ChatResource.class);

    private static final String ENTITY_NAME = "chat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChatRepository chatRepository;
    private final ProfileResource profileResource;
    private final MessageResource messageResource;
    private final ProfileRepository profileRepository;

    @NotGenerated
    public ChatResource(ChatRepository chatRepository, ProfileResource profileResource, MessageResource messageResource,
                        ProfileRepository profileRepository) {
        this.chatRepository = chatRepository;
        this.profileResource = profileResource;
        this.messageResource = messageResource;
        this.profileRepository = profileRepository;
    }

    /**
     * {@code POST  /chats} : Create a new chat.
     *
     * @param chat the chat to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new chat, or with status {@code 400 (Bad Request)} if the chat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Chat> createChat(@RequestBody Chat chat) throws URISyntaxException {
        LOG.debug("REST request to save Chat : {}", chat);
        if (chat.getId() != null) {
            throw new BadRequestAlertException("A new chat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        chat = chatRepository.save(chat);
        return ResponseEntity.created(new URI("/api/chats/" + chat.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, chat.getId().toString()))
            .body(chat);
    }

    /**
     * {@code PUT  /chats/:id} : Updates an existing chat.
     *
     * @param id the id of the chat to save.
     * @param chat the chat to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chat,
     * or with status {@code 400 (Bad Request)} if the chat is not valid,
     * or with status {@code 500 (Internal Server Error)} if the chat couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Chat> updateChat(@PathVariable(value = "id", required = false) final Long id, @RequestBody Chat chat)
        throws URISyntaxException {
        LOG.debug("REST request to update Chat : {}, {}", id, chat);
        if (chat.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chat.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        chat = chatRepository.save(chat);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, chat.getId().toString()))
            .body(chat);
    }

    /**
     * {@code PATCH  /chats/:id} : Partial updates given fields of an existing chat, field will ignore if it is null
     *
     * @param id the id of the chat to save.
     * @param chat the chat to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chat,
     * or with status {@code 400 (Bad Request)} if the chat is not valid,
     * or with status {@code 404 (Not Found)} if the chat is not found,
     * or with status {@code 500 (Internal Server Error)} if the chat couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Chat> partialUpdateChat(@PathVariable(value = "id", required = false) final Long id, @RequestBody Chat chat)
        throws URISyntaxException {
        LOG.debug("REST request to partial update Chat partially : {}, {}", id, chat);
        if (chat.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chat.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Chat> result = chatRepository
            .findById(chat.getId())
            .map(existingChat -> {
                if (chat.getInitiatorName() != null) {
                    existingChat.setInitiatorName(chat.getInitiatorName());
                }
                if (chat.getAccepted() != null) {
                    existingChat.setAccepted(chat.getAccepted());
                }

                return existingChat;
            })
            .map(chatRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, chat.getId().toString())
        );
    }

    @NotGenerated
    @PostMapping("/request-chat-with-profile/{id}")
    public ResponseEntity<Chat> requestFriend(@PathVariable(value="id") Long id) throws URISyntaxException {

        String currentLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        Profile currentProfile = profileRepository.findByUserIsCurrentUser().stream().findFirst().orElse(null);
        Profile requestedProfile = profileResource.getProfile(id).getBody();

        Message message = new Message().content("I would like to chat");
        message = messageResource.createMessage(message).getBody(); // will have initiatorName and time data

        Chat chat = new Chat().initiatorName(currentLogin).accepted(false).addChat(message)
            .addProfile(currentProfile).addProfile(requestedProfile);
        return createChat(chat);
    }

    @NotGenerated
    @PatchMapping(value = "/{id}/accept")
    public ResponseEntity<Profile> acceptChat(@PathVariable(value = "id", required = false) final Long id)
        throws URISyntaxException {
        Chat chat = chatRepository.findById(id).orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        chat.accepted(true);
        chatRepository.save(chat);
        Profile requesterProfile = profileResource.getProfileByLogin(chat.getInitiatorName());
        Profile currentProfile = profileResource.getCurrentUserProfile().getBody();
        currentProfile.befriend(requesterProfile);
        return profileResource.partialUpdateProfile(currentProfile.getId(), currentProfile);
    }

    @NotGenerated
    @PatchMapping(value = "/{id}/message", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Message> messageInChat(@PathVariable(value = "id", required = false) final Long id, @RequestBody Message message)
        throws URISyntaxException {
        Chat chat = chatRepository.findById(id).orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        ResponseEntity<Message> messageResponse = messageResource.createMessage(message);
        chat.addChat(messageResponse.getBody());
        partialUpdateChat(id, chat);
        return messageResponse;
    }

    /**
     * {@code GET  /chats} : get all the chats.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of chats in body.
     */
    @GetMapping("")
    public List<Chat> getAllChats() {
        LOG.debug("REST request to get all Chats");
        return chatRepository.findAllByCurrentProfile(SecurityUtils.getCurrentUserLogin().orElseThrow());
    }

    /**
     * {@code GET  /chats/:id} : get the "id" chat.
     *
     * @param id the id of the chat to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chat, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Chat> getChat(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Chat : {}", id);
        Optional<Chat> chat = chatRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(chat);
    }

    /**
     * {@code DELETE  /chats/:id} : delete the "id" chat.
     *
     * @param id the id of the chat to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Chat : {}", id);
        chatRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
