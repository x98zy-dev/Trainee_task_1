package org.x98zy.user_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.x98zy.user_service.dto.PaymentCardDTO;
import org.x98zy.user_service.dto.UserDTO;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentCardControllerIntegrationTest {

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
    void createCard_ValidData_ReturnsCreated() {
        // Given - сначала создаём пользователя
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Card");
        userDTO.setSurname("Test");
        userDTO.setEmail("card@test.com");

        ResponseEntity<UserDTO> userResponse = restTemplate.postForEntity(
                "/api/users", userDTO, UserDTO.class);
        Long userId = userResponse.getBody().getId();

        // Создаём карту
        PaymentCardDTO cardDTO = new PaymentCardDTO();
        cardDTO.setUserId(userId);
        cardDTO.setNumber("4111111111111111");
        cardDTO.setHolder("CARD HOLDER");
        cardDTO.setExpirationDate(LocalDate.now().plusYears(2));

        // When
        ResponseEntity<PaymentCardDTO> response = restTemplate.postForEntity(
                "/api/cards", cardDTO, PaymentCardDTO.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("4111111111111111", response.getBody().getNumber());
        assertEquals("CARD HOLDER", response.getBody().getHolder());
    }

    @Test
    void createCard_InvalidData_ReturnsBadRequest() {
        // Given
        PaymentCardDTO cardDTO = new PaymentCardDTO(); // Невалидные данные

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/cards", cardDTO, String.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getCardsByUserId_UserExists_ReturnsCards() {
        // Given - создаём пользователя и карту
        UserDTO userDTO = new UserDTO();
        userDTO.setName("GetCards");
        userDTO.setSurname("Test");
        userDTO.setEmail("getcards@test.com");

        ResponseEntity<UserDTO> userResponse = restTemplate.postForEntity(
                "/api/users", userDTO, UserDTO.class);
        Long userId = userResponse.getBody().getId();

        PaymentCardDTO cardDTO = new PaymentCardDTO();
        cardDTO.setUserId(userId);
        cardDTO.setNumber("4222222222222222");
        cardDTO.setHolder("GET CARDS TEST");
        cardDTO.setExpirationDate(LocalDate.now().plusYears(1));

        restTemplate.postForEntity("/api/cards", cardDTO, PaymentCardDTO.class);

        // When
        ResponseEntity<PaymentCardDTO[]> response = restTemplate.getForEntity(
                "/api/cards/user/" + userId, PaymentCardDTO[].class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void activateCard_Success() {
        // Given - создаём пользователя и карту
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Activate");
        userDTO.setSurname("Test");
        userDTO.setEmail("activate@test.com");

        ResponseEntity<UserDTO> userResponse = restTemplate.postForEntity(
                "/api/users", userDTO, UserDTO.class);
        Long userId = userResponse.getBody().getId();

        PaymentCardDTO cardDTO = new PaymentCardDTO();
        cardDTO.setUserId(userId);
        cardDTO.setNumber("4333333333333333");
        cardDTO.setHolder("ACTIVATE TEST");
        cardDTO.setExpirationDate(LocalDate.now().plusYears(1));

        ResponseEntity<PaymentCardDTO> cardResponse = restTemplate.postForEntity(
                "/api/cards", cardDTO, PaymentCardDTO.class);
        Long cardId = cardResponse.getBody().getId();

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/cards/" + cardId + "/activate",
                org.springframework.http.HttpMethod.PATCH,
                null,
                Void.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}