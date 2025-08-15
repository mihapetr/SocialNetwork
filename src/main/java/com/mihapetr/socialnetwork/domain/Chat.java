package com.mihapetr.socialnetwork.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Chat.
 */
@Entity
@Table(name = "chat")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Chat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "initiator_name")
    private String initiatorName;

    @Column(name = "accepted")
    private Boolean accepted;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "chat")
    @JsonIgnoreProperties(value = { "user", "comment", "chat" }, allowSetters = true)
    private Set<Message> chats = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "chats")
    @JsonIgnoreProperties(value = { "posts", "comments", "others", "chats", "profiles" }, allowSetters = true)
    private Set<Profile> profiles = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Chat id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInitiatorName() {
        return this.initiatorName;
    }

    public Chat initiatorName(String initiatorName) {
        this.setInitiatorName(initiatorName);
        return this;
    }

    public void setInitiatorName(String initiatorName) {
        this.initiatorName = initiatorName;
    }

    public Boolean getAccepted() {
        return this.accepted;
    }

    public Chat accepted(Boolean accepted) {
        this.setAccepted(accepted);
        return this;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Set<Message> getChats() {
        return this.chats;
    }

    public void setChats(Set<Message> messages) {
        if (this.chats != null) {
            this.chats.forEach(i -> i.setChat(null));
        }
        if (messages != null) {
            messages.forEach(i -> i.setChat(this));
        }
        this.chats = messages;
    }

    public Chat chats(Set<Message> messages) {
        this.setChats(messages);
        return this;
    }

    public Chat addChat(Message message) {
        this.chats.add(message);
        message.setChat(this);
        return this;
    }

    public Chat removeChat(Message message) {
        this.chats.remove(message);
        message.setChat(null);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Chat user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Profile> getProfiles() {
        return this.profiles;
    }

    public void setProfiles(Set<Profile> profiles) {
        if (this.profiles != null) {
            this.profiles.forEach(i -> i.removeChat(this));
        }
        if (profiles != null) {
            profiles.forEach(i -> i.addChat(this));
        }
        this.profiles = profiles;
    }

    public Chat profiles(Set<Profile> profiles) {
        this.setProfiles(profiles);
        return this;
    }

    public Chat addProfile(Profile profile) {
        this.profiles.add(profile);
        profile.getChats().add(this);
        return this;
    }

    public Chat removeProfile(Profile profile) {
        this.profiles.remove(profile);
        profile.getChats().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Chat)) {
            return false;
        }
        return getId() != null && getId().equals(((Chat) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Chat{" +
            "id=" + getId() +
            ", initiatorName='" + getInitiatorName() + "'" +
            ", accepted='" + getAccepted() + "'" +
            "}";
    }

    void message(String content) {}
}
