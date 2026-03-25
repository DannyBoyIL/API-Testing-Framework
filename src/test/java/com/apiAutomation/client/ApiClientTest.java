package com.apiAutomation.client;

import com.apiAutomation.model.User;
import com.apiAutomation.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ApiClientTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void getUserById() throws IOException, InterruptedException {
        TestLogger.step("GET /users/1");
        ApiResponse<User> response = userService.getUserById(1);
        TestLogger.attach("response.user", String.valueOf(response.body()));

        ApiAssertions.assertStatusCode(response, 200);
        ApiAssertions.assertHeaderContains(response, "content-type", "application/json");
        assertUserBasics(response.body());
    }

    @Test
    void createUser() throws IOException, InterruptedException {
        TestLogger.step("POST /users");
        User payload = buildUser("John Doe", "jdoe", "john@example.com");

        ApiResponse<User> response = userService.createUser(payload);
        TestLogger.attach("request.user", String.valueOf(payload));
        TestLogger.attach("response.user", String.valueOf(response.body()));

        ApiAssertions.assertStatusCode(response, 201);
        assertUserBasics(response.body());
    }

    @Test
    void getMissingUser() throws IOException, InterruptedException {
        TestLogger.step("GET /users/999999");
        ApiResponse<User> response = userService.getUserById(999999);
        TestLogger.attach("response.user", String.valueOf(response.body()));

        ApiAssertions.assertStatusCode(response, 404);
    }

    @Test
    void getInvalidEndpoint() throws IOException, InterruptedException {
        TestLogger.step("GET /invalidEndpoint");
        ApiResponse<String> response = userService.getInvalidEndpoint("/invalidEndpoint");
        TestLogger.attach("response.body", response.body());

        ApiAssertions.assertStatusCode(response, 404);
        ApiAssertions.assertBodyContains(response.body(), "{}");
    }

    @Test
    void updateUser() throws IOException, InterruptedException {
        TestLogger.step("PUT /users/1");
        User payload = buildUser("Jane Doe", "jane", "jane@example.com");

        ApiResponse<User> response = userService.updateUser(1, payload);
        TestLogger.attach("request.user", String.valueOf(payload));
        TestLogger.attach("response.user", String.valueOf(response.body()));

        ApiAssertions.assertStatusCode(response, 200);
        assertUserBasics(response.body());
    }

    @Test
    void deleteUser() throws IOException, InterruptedException {
        TestLogger.step("DELETE /users/1");
        ApiResponse<Void> response = userService.deleteUser(1);

        ApiAssertions.assertStatusCodeIn(response, 200, 204);
    }

    private User buildUser(String name, String username, String email) {
        User user = new User();
        user.name = name;
        user.username = username;
        user.email = email;
        user.phone = "1-770-736-8031 x56442";
        user.website = "example.com";
        return user;
    }

    private void assertUserBasics(User user) {
        Assertions.assertNotNull(user, "Expected user but got null");
        Assertions.assertFalse(user.name == null || user.name.isBlank(), "Expected name");
        Assertions.assertFalse(user.username == null || user.username.isBlank(), "Expected username");
        Assertions.assertFalse(user.email == null || user.email.isBlank(), "Expected email");
    }
}
