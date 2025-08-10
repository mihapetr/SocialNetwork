
package com.mihapetr.socialnetwork.cucumber.stepdefs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mihapetr.socialnetwork.domain.User;
import com.mihapetr.socialnetwork.web.rest.AuthenticateController;
import com.mihapetr.socialnetwork.web.rest.vm.LoginVM;
import com.mihapetr.socialnetwork.web.rest.vm.ManagedUserVM;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

public class Account extends Common{

    static boolean done  = false;

    @Before
    public void before() {
        if (done) return;
        setup();
        done = true;
    }

    private final String newLogin = "myUsername";
    private final String newPassword = "myPassword";
    private final String newEmail = "test@mail.com";

	@Given("user does not have account")
	public void user_does_not_have_account() {
        assert userRepository.findOneByLogin(newLogin).isEmpty() : "'mYUsername' account exists";
	}

    private ResponseEntity<Void> registrationResponse;

	@When("user submits registration form")
	public void user_submits_registration_form() {
        ManagedUserVM registrationForm = new ManagedUserVM();
        registrationForm.setLogin(newLogin);
        registrationForm.setEmail(newEmail);
        registrationForm.setPassword(newPassword);

        registrationResponse = restClient
            .post()
            .uri("http://localhost:" + port + "/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(registrationForm)
            .retrieve().toBodilessEntity();
	}

	@Then("user profile is created")
	public void user_profile_is_created() {
        assert userRepository.findOneByLogin(newLogin).isPresent() : "user with login=" + newLogin + " did not get created";
	}

	@And("user is notified about success")
	public void user_is_notified_about_success() {
        assert registrationResponse.getStatusCode().equals(HttpStatus.CREATED) :
            "Registration response is not CREATED; response = " + registrationResponse.toString();
	}

	@Given("credentials are valid")
	public void credentials_are_valid() {
        loginForm.setUsername(existingLogin);
        loginForm.setPassword(existingPassword);
        User user = userRepository.findOneByLogin(existingLogin).orElseThrow();
        assert user.getLogin().equals(existingLogin) :
            "user with login=" + existingLogin + " and password=" + existingPassword + " did not match DB";

	}

    private ResponseEntity<AuthenticateController.JWTToken> loginResponse;
    LoginVM loginForm = new LoginVM();
    private String actualUnauthorizedBodyJson;

	@When("user submits login form")
	public void user_submits_login_form() {
        try {
            loginResponse = signIn(loginForm);
        } catch (HttpClientErrorException.Unauthorized ex) {
            actualUnauthorizedBodyJson = ex.getResponseBodyAsString();
        }
	}

	@Then("user is successfully logged in")
	public void user_is_logged_in() {
        assert loginResponse.getBody().getIdToken() != null :
            "user with login=" + newLogin + " did not get logged in";
	}

	@Given("credentials are invalid")
	public void credentials_are_invalid() {
        loginForm.setUsername(existingLogin);
        loginForm.setPassword("gibberish");
        User user = userRepository.findOneByLogin(existingLogin).orElseThrow();
        assert !user.getPassword().equals(existingPassword) :
            "user with login=" + existingLogin + " and password= 'gibberish' matched DB";
	}

    ObjectMapper mapper = new ObjectMapper();

	@Then("login is rejected")
	public void login_is_rejected() throws JsonProcessingException {
        JsonNode actualNode = mapper.readTree(actualUnauthorizedBodyJson);
        assert "401".equals(actualNode.get("status").asText()) : "expected status 401 from server, got " +
            actualNode.get("status").asText();
	}

	@Then("user is notified about failure")
	public void user_is_notified_about_failure() throws JsonProcessingException {
        JsonNode actualNode = mapper.readTree(actualUnauthorizedBodyJson);
        assert "Bad credentials".equals(actualNode.get("detail").asText()) : "expected 'Bad credentials' from server, got " +
            actualNode.get("detail").asText();
	}

}
