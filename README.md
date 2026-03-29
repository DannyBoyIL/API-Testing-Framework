# Native API Testing in Pure Java (Java · HttpClient · JUnit · Allure)
A compact portfolio project that demonstrates protocol-level API testing without framework abstraction. It uses Java's built-in `HttpClient`, manual assertions, and a minimal custom JSON parser to show how HTTP requests and responses are handled under the hood.

## Feature Under Test
This suite targets the JSONPlaceholder public API and focuses on **Users**:
* `GET /users/{id}` → validate status, headers, and parsed fields
* `POST /users` → validate create flow and echoed payload
* `PUT /users/{id}` → validate update flow
* `DELETE /users/{id}` → validate delete flow
* `GET /invalidEndpoint` → validate 404 handling

## Highlights
* **Plain Java HTTP** via `java.net.http.HttpClient` — no RestAssured, no Spring
* **Manual assertions** to show what frameworks abstract away
* **Custom minimal JSON parser** (`JsonParser`) for protocol-level transparency
* **OOP structure** with `UserService` abstraction
* **Allure reporting** + structured logs

## Tech Stack
* Java 17
* Maven
* JUnit 5
* Allure
* SLF4J + Logback

## Quick Start
```bash
# Run tests
mvn test

# Generate Allure report
mvn allure:report
```
Open:
`target/site/allure-maven-plugin/index.html`

Serve report locally:
```bash
mvn allure:serve
```

## Example Tests
The following tests cover the full CRUD surface of the JSONPlaceholder Users API. Each test uses `TestLogger` to attach request/response details to the Allure report and `ApiAssertions` for readable, custom assertions:

```java
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

    ApiAssertions.assertStatusCode(response, 201);
    assertUserBasics(response.body());
}

@Test
void getMissingUser() throws IOException, InterruptedException {
    TestLogger.step("GET /users/999999");
    ApiResponse<User> response = userService.getUserById(999999);

    ApiAssertions.assertStatusCode(response, 404);
}

@Test
void deleteUser() throws IOException, InterruptedException {
    TestLogger.step("DELETE /users/1");
    ApiResponse<Void> response = userService.deleteUser(1);

    // Custom assertion handles 200 or 204 — both are valid DELETE responses
    ApiAssertions.assertStatusCodeIn(response, 200, 204);
}
```

`assertUserBasics` is a shared private helper that validates the three required fields (`name`, `username`, `email`) across multiple tests without duplication. `assertStatusCodeIn` handles cases where the server legitimately returns one of several valid status codes — a pattern frameworks like RestAssured implement internally but here is made explicit.


## Project Structure
```text
api-automation/
│
├── src/main/java/com/apiAutomation/
│   ├── client/                 # ApiClient + ApiResponse
│   ├── config/                 # Config (base URL)
│   ├── model/                  # User model
│   ├── service/                # UserService (API operations)
│   └── util/                   # JsonParser (minimal parser)
│
├── src/test/java/com/apiAutomation/
│   ├── client/                 # ApiClientTest + assertions + logging
│   └── util/                   # JsonParserTest
│
├── src/test/resources/
│   └── logback-test.xml         # Test logging config
│
├── pom.xml
└── README.md
```

## Why No Jackson
This project intentionally avoids JSON libraries to demonstrate manual parsing and mapping basics.

`JsonParser` supports:
* Flat JSON objects only
* Manual type conversion (`String`, `int`, `boolean`)
* Simple Map serialization to JSON

Nested arrays/objects are intentionally skipped by design.

## Test Output
* Allure results: `target/allure-results/`
* Surefire reports: `target/surefire-reports/`
* Log file: `target/test-logs/test.log`
* Allure HTML report: `target/site/allure-maven-plugin/index.html`
