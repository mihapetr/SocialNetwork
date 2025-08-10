
package com.mihapetr.socialnetwork.cucumber.stepdefs;

import com.mihapetr.socialnetwork.domain.Chat;
import com.mihapetr.socialnetwork.domain.Post;
import com.mihapetr.socialnetwork.domain.Profile;
import com.mihapetr.socialnetwork.repository.PostRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class Posting extends Common {

    @Autowired
    PostRepository postRepository;
    static boolean done = false;

    @Before
    public void before() {
        user = userRepository.findOneByLogin(existingLogin).orElse(null);
        if (done) return;
        setup();
        user = userRepository.findOneByLogin(existingLogin).orElse(null);
        createUserProfile("firstobserver");
        createUserProfile("secondobserver");
        done = true;
    }

	@Given("user is logged in : posting")
	public void user_is_logged_in___posting() {
        token = signIn(existingLogin, existingPassword);
        assert token != null : "Sign in token is null";
	}

    List<Post> postsResponse;

	@Given("user navigates to Posts view")
	public void user_navigates_to_Posts_view() {
        postsResponse = restClient.get()
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
        newPost = new Post().description("This is my first description :)");

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
        
	}

	@When("user navigates to their Profile view")
	public void user_navigates_to_their_Profile_view() {

	}

	@Then("user can see all of their Posts")
	public void user_can_see_all_of_their_Posts() {

	}

	@And("user selects details of one of the Posts")
	public void user_selects_details_of_one_of_the_Posts() {

	}

	@When("user submits the comment form")
	public void user_submits_the_comment_form() {

	}

	@Then("comment is added to the Post")
	public void comment_is_added_to_the_Post() {

	}

	@And("user details are visible in the post")
	public void user_details_are_visible_in_the_post() {

	}


}
