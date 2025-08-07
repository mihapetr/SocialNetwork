package com.mihapetr.socialnetwork.cucumber.stepdefs;

import com.mihapetr.socialnetwork.repository.UserRepository;
import com.mihapetr.socialnetwork.service.UserService;
import com.mihapetr.socialnetwork.service.dto.AdminUserDTO;
import com.mihapetr.socialnetwork.web.rest.vm.LoginVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import com.mihapetr.socialnetwork.web.rest.AuthenticateController;

public class Common {

    @LocalServerPort
    protected int port;

    @Autowired
    protected RestClient restClient;
    RestClient.ResponseSpec res;

    @Autowired
    protected UserService userService;

    @Autowired
    UserRepository userRepository;

    protected String token;

    protected final String existingLogin = "existinglogin";
    protected final String existingPassword = "existingpassword";

    public void setup() {
        if (userRepository.findOneByLogin(existingLogin).isEmpty()) {
            AdminUserDTO user = new AdminUserDTO();
            user.setLogin(existingLogin);
            user.setEmail("existing@email.com");
            userService.registerUser(user, existingPassword);
        }
    }

    protected ResponseEntity<AuthenticateController.JWTToken> signIn(LoginVM loginForm) throws HttpClientErrorException {
        return restClient
            .post()
            .uri("http://localhost:" + port + "/api/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginForm)
            .retrieve()
            .toEntity(AuthenticateController.JWTToken.class);
    }

    protected String signIn(String username, String password) throws HttpClientErrorException {
        LoginVM loginForm = new LoginVM();
        loginForm.setUsername(username);
        loginForm.setPassword(password);
        return signIn(loginForm).getBody().getIdToken();
    }
}
