package com.mihapetr.socialnetwork.web.rest;

import com.mihapetr.socialnetwork.NotGenerated;
import com.mihapetr.socialnetwork.domain.Comment;
import com.mihapetr.socialnetwork.domain.Message;
import com.mihapetr.socialnetwork.domain.Post;
import com.mihapetr.socialnetwork.repository.PostRepository;
import com.mihapetr.socialnetwork.repository.ProfileRepository;
import com.mihapetr.socialnetwork.security.SecurityUtils;
import com.mihapetr.socialnetwork.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mihapetr.socialnetwork.domain.Post}.
 */
@RestController
@RequestMapping("/api/posts")
@Transactional
public class PostResource {

    private static final Logger LOG = LoggerFactory.getLogger(PostResource.class);

    private static final String ENTITY_NAME = "post";
    private final ProfileRepository profileRepository;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PostRepository postRepository;
    CommentResource commentResource;

    public PostResource(PostRepository postRepository, ProfileRepository profileRepository, CommentResource commentResource) {
        this.postRepository = postRepository;
        this.profileRepository = profileRepository;
        this.commentResource = commentResource;
    }

    /**
     * {@code POST  /posts} : Create a new post.
     *
     * @param post the post to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new post, or with status {@code 400 (Bad Request)} if the post has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Post> createPost(@Valid @RequestBody Post post) throws URISyntaxException {
        LOG.debug("REST request to save Post : {}", post);
        if (post.getId() != null) {
            throw new BadRequestAlertException("A new post cannot already have an ID", ENTITY_NAME, "idexists");
        }
        customCreatePost(post);
        post = postRepository.save(post);
        post.getProfile().getUser();
        return ResponseEntity.created(new URI("/api/posts/" + post.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, post.getId().toString()))
            .body(post);
    }

    @NotGenerated
    void customCreatePost(Post post) {
        post.time(ZonedDateTime.now());
        post.setProfile(
            profileRepository
                .findByUserLogin(
                    SecurityUtils.getCurrentUserLogin()
                        .orElseThrow(() -> new BadRequestAlertException("Could not get current login", ENTITY_NAME, "currentLoginFail"))
                )
                .orElseThrow(() -> new BadRequestAlertException("Profile not found", ENTITY_NAME, "profilenotfound"))
        );
    }

    /**
     * {@code PUT  /posts/:id} : Updates an existing post.
     *
     * @param id the id of the post to save.
     * @param post the post to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated post,
     * or with status {@code 400 (Bad Request)} if the post is not valid,
     * or with status {@code 500 (Internal Server Error)} if the post couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Post post)
        throws URISyntaxException {
        LOG.debug("REST request to update Post : {}, {}", id, post);
        if (post.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, post.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        post = postRepository.save(post);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, post.getId().toString()))
            .body(post);
    }

    /**
     * {@code PATCH  /posts/:id} : Partial updates given fields of an existing post, field will ignore if it is null
     *
     * @param id the id of the post to save.
     * @param post the post to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated post,
     * or with status {@code 400 (Bad Request)} if the post is not valid,
     * or with status {@code 404 (Not Found)} if the post is not found,
     * or with status {@code 500 (Internal Server Error)} if the post couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Post> partialUpdatePost(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Post post
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Post partially : {}, {}", id, post);
        if (post.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, post.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Post> result = postRepository
            .findById(post.getId())
            .map(existingPost -> {
                if (post.getImage() != null) {
                    existingPost.setImage(post.getImage());
                }
                if (post.getImageContentType() != null) {
                    existingPost.setImageContentType(post.getImageContentType());
                }
                if (post.getDescription() != null) {
                    existingPost.setDescription(post.getDescription());
                }
                if (post.getTime() != null) {
                    existingPost.setTime(post.getTime());
                }

                return existingPost;
            })
            .map(postRepository::save);
        result
            .orElseThrow()
            .setComments(
                result
                    .orElseThrow()
                    .getComments()
                    .stream()
                    .map(c -> {
                        c.setParent(c.getParent());
                        return c;
                    })
                    .collect(Collectors.toSet())
            );
        System.out.println(
            "Post (result) comments after patching: " +
            result.orElseThrow().getComments().stream().map(Comment::getParent).collect(Collectors.toSet())
        );
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, post.getId().toString())
        );
    }

    // rquires a message with content
    @NotGenerated
    @PatchMapping(value = "/{id}/comment", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Post> commentOnPost(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Message message
    ) throws URISyntaxException {
        String currentLogin = SecurityUtils.getCurrentUserLogin().orElseThrow();
        message.time(ZonedDateTime.now()).senderName(currentLogin);
        Comment comment = new Comment().parent(message).profile(profileRepository.findByUserLogin(currentLogin).orElseThrow());
        Post post = postRepository.findById(id).orElseThrow();
        post.comment(comment);
        comment = commentResource.createComment(comment).getBody();
        System.out.println("Comment before patching post: " + comment);
        System.out.println("Comment content before patching post: " + comment.getParent().getContent());
        return partialUpdatePost(id, post);
    }

    /**
     * {@code GET  /posts} : get all the posts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of posts in body.
     */
    @GetMapping("")
    public List<Post> getAllPosts() {
        LOG.debug("REST request to get all Posts");
        return postRepository.findAll();
    }

    /**
     * {@code GET  /posts/:id} : get the "id" post.
     *
     * @param id the id of the post to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the post, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Post : {}", id);
        Optional<Post> post = postRepository.findById(id);
        fetchEntities(post.orElseThrow());
        System.out.println("Post comments before returning it: " + post.orElseThrow().getComments());
        System.out.println(
            "Post messages before returning it: " +
            post.orElseThrow().getComments().stream().map(Comment::getParent).collect(Collectors.toSet())
        );
        return ResponseUtil.wrapOrNotFound(post);
    }

    @NotGenerated
    void fetchEntities(Post post) {
        post.setProfile(post.getProfile());
        post.setComments(
            post
                .getComments()
                .stream()
                .map(comment -> {
                    comment.setParent(comment.getParent());
                    comment.setProfile(comment.getProfile());
                    return comment;
                })
                .collect(Collectors.toSet())
        );
    }

    /**
     * {@code DELETE  /posts/:id} : delete the "id" post.
     *
     * @param id the id of the post to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Post : {}", id);
        postRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
