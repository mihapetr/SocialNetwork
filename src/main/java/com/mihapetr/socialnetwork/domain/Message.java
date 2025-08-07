package com.mihapetr.socialnetwork.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * A Message.
 */
@Entity
@Table(name = "message")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "content")
    private String content;

    @Column(name = "time")
    private ZonedDateTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JsonIgnoreProperties(value = { "parent", "user", "post", "profile" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "parent")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "chats", "user", "profiles" }, allowSetters = true)
    private Chat chat;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Message id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public Message senderName(String senderName) {
        this.setSenderName(senderName);
        return this;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return this.content;
    }

    public Message content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getTime() {
        return this.time;
    }

    public Message time(ZonedDateTime time) {
        this.setTime(time);
        return this;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Message user(User user) {
        this.setUser(user);
        return this;
    }

    public Comment getComment() {
        return this.comment;
    }

    public void setComment(Comment comment) {
        if (this.comment != null) {
            this.comment.setParent(null);
        }
        if (comment != null) {
            comment.setParent(this);
        }
        this.comment = comment;
    }

    public Message comment(Comment comment) {
        this.setComment(comment);
        return this;
    }

    public Chat getChat() {
        return this.chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Message chat(Chat chat) {
        this.setChat(chat);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        return getId() != null && getId().equals(((Message) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Message{" +
            "id=" + getId() +
            ", senderName='" + getSenderName() + "'" +
            ", content='" + getContent() + "'" +
            ", time='" + getTime() + "'" +
            "}";
    }
}
