package com.mihapetr.socialnetwork.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Profile.
 */
@Entity
@Table(name = "profile")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Profile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "status")
    private String status;

    @Lob
    @Column(name = "picture")
    private byte[] picture;

    @Column(name = "picture_content_type")
    private String pictureContentType;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile")
    @JsonIgnoreProperties(value = { "comments", "user", "profile" }, allowSetters = true)
    private Set<Post> posts = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile")
    @JsonIgnoreProperties(value = { "parent", "user", "post", "profile" }, allowSetters = true)
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_profile__other",
        joinColumns = @JoinColumn(name = "profile_id"),
        inverseJoinColumns = @JoinColumn(name = "other_id")
    )
    @JsonIgnoreProperties(value = { "posts", "comments", "user", "others", "chats", "profiles" }, allowSetters = true)
    private Set<Profile> others = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_profile__chat",
        joinColumns = @JoinColumn(name = "profile_id"),
        inverseJoinColumns = @JoinColumn(name = "chat_id")
    )
    @JsonIgnoreProperties(value = { "chats", "user", "profiles" }, allowSetters = true)
    private Set<Chat> chats = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "others")
    @JsonIgnoreProperties(value = { "posts", "comments", "user", "others", "chats", "profiles" }, allowSetters = true)
    private Set<Profile> profiles = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Profile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return this.status;
    }

    public Profile status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public byte[] getPicture() {
        return this.picture;
    }

    public Profile picture(byte[] picture) {
        this.setPicture(picture);
        return this;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getPictureContentType() {
        return this.pictureContentType;
    }

    public Profile pictureContentType(String pictureContentType) {
        this.pictureContentType = pictureContentType;
        return this;
    }

    public void setPictureContentType(String pictureContentType) {
        this.pictureContentType = pictureContentType;
    }

    public Set<Post> getPosts() {
        return this.posts;
    }

    public void setPosts(Set<Post> posts) {
        if (this.posts != null) {
            this.posts.forEach(i -> i.setProfile(null));
        }
        if (posts != null) {
            posts.forEach(i -> i.setProfile(this));
        }
        this.posts = posts;
    }

    public Profile posts(Set<Post> posts) {
        this.setPosts(posts);
        return this;
    }

    public Profile addPost(Post post) {
        this.posts.add(post);
        post.setProfile(this);
        return this;
    }

    public Profile removePost(Post post) {
        this.posts.remove(post);
        post.setProfile(null);
        return this;
    }

    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setProfile(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setProfile(this));
        }
        this.comments = comments;
    }

    public Profile comments(Set<Comment> comments) {
        this.setComments(comments);
        return this;
    }

    public Profile addComment(Comment comment) {
        this.comments.add(comment);
        comment.setProfile(this);
        return this;
    }

    public Profile removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setProfile(null);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Profile user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Profile> getOthers() {
        return this.others;
    }

    public void setOthers(Set<Profile> profiles) {
        this.others = profiles;
    }

    public Profile others(Set<Profile> profiles) {
        this.setOthers(profiles);
        return this;
    }

    public Profile addOther(Profile profile) {
        this.others.add(profile);
        return this;
    }

    public Profile removeOther(Profile profile) {
        this.others.remove(profile);
        return this;
    }

    public Set<Chat> getChats() {
        return this.chats;
    }

    public void setChats(Set<Chat> chats) {
        this.chats = chats;
    }

    public Profile chats(Set<Chat> chats) {
        this.setChats(chats);
        return this;
    }

    public Profile addChat(Chat chat) {
        this.chats.add(chat);
        return this;
    }

    public Profile removeChat(Chat chat) {
        this.chats.remove(chat);
        return this;
    }

    public Set<Profile> getProfiles() {
        return this.profiles;
    }

    public void setProfiles(Set<Profile> profiles) {
        if (this.profiles != null) {
            this.profiles.forEach(i -> i.removeOther(this));
        }
        if (profiles != null) {
            profiles.forEach(i -> i.addOther(this));
        }
        this.profiles = profiles;
    }

    public Profile profiles(Set<Profile> profiles) {
        this.setProfiles(profiles);
        return this;
    }

    public Profile addProfile(Profile profile) {
        this.profiles.add(profile);
        profile.getOthers().add(this);
        return this;
    }

    public Profile removeProfile(Profile profile) {
        this.profiles.remove(profile);
        profile.getOthers().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Profile)) {
            return false;
        }
        return getId() != null && getId().equals(((Profile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Profile{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", picture='" + getPicture() + "'" +
            ", pictureContentType='" + getPictureContentType() + "'" +
            "}";
    }
}
