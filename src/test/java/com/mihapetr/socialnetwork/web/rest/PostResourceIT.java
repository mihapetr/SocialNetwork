package com.mihapetr.socialnetwork.web.rest;

import static com.mihapetr.socialnetwork.domain.PostAsserts.*;
import static com.mihapetr.socialnetwork.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mihapetr.socialnetwork.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mihapetr.socialnetwork.IntegrationTest;
import com.mihapetr.socialnetwork.domain.Post;
import com.mihapetr.socialnetwork.domain.Profile;
import com.mihapetr.socialnetwork.repository.PostRepository;
import com.mihapetr.socialnetwork.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PostResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PostResourceIT {

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/posts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPostMockMvc;

    private Post post;

    private Post insertedPost;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Post createEntity(EntityManager em) {
        Post post = new Post()
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .description(DEFAULT_DESCRIPTION)
            .time(DEFAULT_TIME);
        // Add required entity
        Profile profile;
        if (TestUtil.findAll(em, Profile.class).isEmpty()) {
            profile = ProfileResourceIT.createEntity();
            em.persist(profile);
            em.flush();
        } else {
            profile = TestUtil.findAll(em, Profile.class).get(0);
        }
        post.setProfile(profile);
        return post;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Post createUpdatedEntity(EntityManager em) {
        Post updatedPost = new Post()
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION)
            .time(UPDATED_TIME);
        // Add required entity
        Profile profile;
        if (TestUtil.findAll(em, Profile.class).isEmpty()) {
            profile = ProfileResourceIT.createUpdatedEntity();
            em.persist(profile);
            em.flush();
        } else {
            profile = TestUtil.findAll(em, Profile.class).get(0);
        }
        updatedPost.setProfile(profile);
        return updatedPost;
    }

    @BeforeEach
    public void initTest() {
        post = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedPost != null) {
            postRepository.delete(insertedPost);
            insertedPost = null;
        }
    }

    @Test
    @Transactional
    void createPost() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Post
        var returnedPost = om.readValue(
            restPostMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(post)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Post.class
        );

        // Validate the Post in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPostUpdatableFieldsEquals(returnedPost, getPersistedPost(returnedPost));

        insertedPost = returnedPost;
    }

    @Test
    @Transactional
    void createPostWithExistingId() throws Exception {
        // Create the Post with an existing ID
        post.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(post)))
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPosts() throws Exception {
        // Initialize the database
        insertedPost = postRepository.saveAndFlush(post);

        // Get all the postList
        restPostMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].time").value(hasItem(sameInstant(DEFAULT_TIME))));
    }

    @Test
    @Transactional
    void getPost() throws Exception {
        // Initialize the database
        insertedPost = postRepository.saveAndFlush(post);

        // Get the post
        restPostMockMvc
            .perform(get(ENTITY_API_URL_ID, post.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(post.getId().intValue()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64.getEncoder().encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.time").value(sameInstant(DEFAULT_TIME)));
    }

    @Test
    @Transactional
    void getNonExistingPost() throws Exception {
        // Get the post
        restPostMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPost() throws Exception {
        // Initialize the database
        insertedPost = postRepository.saveAndFlush(post);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the post
        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPost are not directly saved in db
        em.detach(updatedPost);
        updatedPost.image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE).description(UPDATED_DESCRIPTION).time(UPDATED_TIME);

        restPostMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPost.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPost))
            )
            .andExpect(status().isOk());

        // Validate the Post in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPostToMatchAllProperties(updatedPost);
    }

    @Test
    @Transactional
    void putNonExistingPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        post.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(put(ENTITY_API_URL_ID, post.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(post)))
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        post.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(post))
            )
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        post.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(post)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Post in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePostWithPatch() throws Exception {
        // Initialize the database
        insertedPost = postRepository.saveAndFlush(post);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the post using partial update
        Post partialUpdatedPost = new Post();
        partialUpdatedPost.setId(post.getId());

        restPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPost.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPost))
            )
            .andExpect(status().isOk());

        // Validate the Post in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPostUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPost, post), getPersistedPost(post));
    }

    @Test
    @Transactional
    void fullUpdatePostWithPatch() throws Exception {
        // Initialize the database
        insertedPost = postRepository.saveAndFlush(post);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the post using partial update
        Post partialUpdatedPost = new Post();
        partialUpdatedPost.setId(post.getId());

        partialUpdatedPost
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .description(UPDATED_DESCRIPTION)
            .time(UPDATED_TIME);

        restPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPost.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPost))
            )
            .andExpect(status().isOk());

        // Validate the Post in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPostUpdatableFieldsEquals(partialUpdatedPost, getPersistedPost(partialUpdatedPost));
    }

    @Test
    @Transactional
    void patchNonExistingPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        post.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(patch(ENTITY_API_URL_ID, post.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(post)))
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        post.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(post))
            )
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        post.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(post)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Post in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePost() throws Exception {
        // Initialize the database
        insertedPost = postRepository.saveAndFlush(post);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the post
        restPostMockMvc
            .perform(delete(ENTITY_API_URL_ID, post.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return postRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Post getPersistedPost(Post post) {
        return postRepository.findById(post.getId()).orElseThrow();
    }

    protected void assertPersistedPostToMatchAllProperties(Post expectedPost) {
        assertPostAllPropertiesEquals(expectedPost, getPersistedPost(expectedPost));
    }

    protected void assertPersistedPostToMatchUpdatableProperties(Post expectedPost) {
        assertPostAllUpdatablePropertiesEquals(expectedPost, getPersistedPost(expectedPost));
    }
}
