
package com.mihapetr.socialnetwork.cucumber.stepdefs;

import com.mihapetr.socialnetwork.domain.*;
import com.mihapetr.socialnetwork.repository.PostRepository;
import com.mihapetr.socialnetwork.web.rest.errors.BadRequestAlertException;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;
import java.util.List;

public class Posting extends Common {

    @Autowired
    PostRepository postRepository;
    static boolean done = false;
    static Long thirdScenarioPostId;

    Long createUserPost(String description) {
        Post post = new Post().description(description).time(ZonedDateTime.now()).profile(
            profileRepository.findByUserLogin(existingLogin).orElseThrow(() -> new RuntimeException("User not found when creating user post"))
        );
        return postRepository.save(post).getId();
    }

    @Before
    public void before() {
        user = userRepository.findOneByLogin(existingLogin).orElse(null);
        if (done) return;
        setup();
        user = userRepository.findOneByLogin(existingLogin).orElse(null);
        createUserProfile("firstobserver");
        createUserProfile("secondobserver");
        createUserPost("This exists for the second scenario");
        thirdScenarioPostId = createUserPost("This exists for the third scenario");
        done = true;
    }

	@Given("user is logged in : posting")
	public void user_is_logged_in___posting() {
        token = signIn(existingLogin, existingPassword);
        assert token != null : "Sign in token is null";
	}

    List<Post> allPostsResponse;

	@Given("user navigates to Posts view")
	public void user_navigates_to_Posts_view() {
        allPostsResponse = restClient.get()
            .uri("http://localhost:" + port + "/api/posts")
            .header("Authorization", "Bearer " + token)
            .retrieve().toEntity(new ParameterizedTypeReference<List<Post>>() {}).getBody();
	}

    String url;

	@And("user selects new Post option")
	public void user_selects_new_Post_option() {
        url = "http://localhost:" + port + "/api/posts";
	}

    Post newPost;

	@And("user fills the new post form")
	public void user_fills_the_new_post_form() {
        newPost = new Post().description("This is my first description :)")
            .profile(new Profile());    // for validators, an empty profile
	}

    ResponseEntity<Post> newPostResponse;

	@When("user submits the new post form")
	public void user_submits_the_new_post_form() {
        newPostResponse = restClient.post()
            .uri(url)
            .header("Authorization", "Bearer " + token)
            .body(newPost)
            .retrieve().toEntity(Post.class);
	}

	@Then("the post is created on the platform")
	public void the_post_is_created_on_the_platform() {
        assert newPostResponse.getStatusCode() == HttpStatus.CREATED : "Expected CREATED, got " + newPostResponse.getStatusCode();
        assert postRepository.findById(newPostResponse.getBody().getId()).isPresent() : "Post not found in DB";
	}

	@And("all users can see the post")
	public void all_users_can_see_the_post() {
        String token1 = signIn("firstobserver", "firstobserverpass");
        String token2 = signIn("secondobserver", "secondobserverpass");
        assert token1 != null && token2 != null : "At least one of the observer tokens is null; token1: " + token1 + ", token2: " + token2;
	}

	@And("user is post owner")
	public void user_is_post_owner() {
        String newPostUserLogin = newPostResponse.getBody().getProfile().getUser().getLogin();
        assert newPostUserLogin.equals(existingLogin) : "Unxpected owner of new post; owner: " + newPostUserLogin;
	}

    Profile userProfile;

	@When("user navigates to their Profile view")
	public void user_navigates_to_their_Profile_view() {
        userProfile = restClient.get().uri("http://localhost:" + port + "/api/profiles/current-user")
            .header("Authorization", "Bearer " + token)
            .retrieve().toEntity(Profile.class).getBody();
	}

	@Then("user can see all of their Posts")
	public void user_can_see_all_of_their_Posts() {
        assert userProfile.getPosts().stream().anyMatch(post -> post.getDescription().equals("This exists for the second scenario")) :
            "Missing post after navigating to user profile; posts = " + userProfile.getPosts();
	}

    Post postDetails;

	@And("user selects details of one of the Posts")
	public void user_selects_details_of_one_of_the_Posts() {
        assert allPostsResponse.stream().anyMatch(post -> post.getId().equals(thirdScenarioPostId));
        postDetails = restClient.get().uri("http://localhost:" + port + "/api/posts/" + thirdScenarioPostId)
            .header("Authorization", "Bearer " + token).retrieve().toEntity(Post.class).getBody();
	}

    Post postAfterComment;

	@When("user submits the comment form")
	public void user_submits_the_comment_form() {
        Message newCommentMessage = new Message().content("This is a cool post!");
        postAfterComment = restClient.patch().uri("http://localhost:" + port + "/api/posts/" + thirdScenarioPostId + "/comment")
            .header("Authorization", "Bearer " + token)
            .body(newCommentMessage)
            .retrieve().toEntity(Post.class).getBody();
	}

	@Then("comment is added to the Post")
	public void comment_is_added_to_the_Post() {
        postAfterComment = restClient.get().uri("http://localhost:" + port + "/api/posts/" + thirdScenarioPostId)
            .header("Authorization", "Bearer " + token)
            .retrieve().toEntity(Post.class).getBody();
        assert postAfterComment.getComments().stream().anyMatch(comment -> comment.getParent().getContent().equals("This is a cool post!")) :
            "Expected comment not in post comments; comments = " + postAfterComment.getComments();
	}

	@And("user details are visible in the post")
	public void user_details_are_visible_in_the_post() {
        assert postAfterComment.getComments().stream().anyMatch(comment -> comment.getProfile().getUser().getLogin().equals(existingLogin)) :
            "User details not visible in the post comments; comments = " + postAfterComment.getComments();
	}


}
