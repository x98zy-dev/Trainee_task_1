package org.x98zy.user_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.x98zy.user_service.dto.UserDTO;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createUser_ValidData_ReturnsCreated() {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Integration");
        userDTO.setSurname("Test");
        userDTO.setEmail("integration@test.com");

        // When
        ResponseEntity<UserDTO> response = restTemplate.postForEntity(
                "/api/users", userDTO, UserDTO.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Integration", response.getBody().getName());
        assertEquals("integration@test.com", response.getBody().getEmail());
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        // Given - сначала создаём пользователя
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Get");
        userDTO.setSurname("Test");
        userDTO.setEmail("get@test.com");

        ResponseEntity<UserDTO> createResponse = restTemplate.postForEntity(
                "/api/users", userDTO, UserDTO.class);
        Long userId = createResponse.getBody().getId();

        // When
        ResponseEntity<UserDTO> response = restTemplate.getForEntity(
                "/api/users/" + userId, UserDTO.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Get", response.getBody().getName());
    }

    @Test
    void getUserById_UserNotFound_ReturnsNotFound() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/users/999", String.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}