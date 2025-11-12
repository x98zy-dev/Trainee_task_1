package org.x98zy.user_service.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.x98zy.user_service.dto.PaymentCardDTO;
import org.x98zy.user_service.service.PaymentCardDtoService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cards")
public class PaymentCardController {

    private final PaymentCardDtoService paymentCardDtoService;

    public PaymentCardController(PaymentCardDtoService paymentCardDtoService) {
        this.paymentCardDtoService = paymentCardDtoService;
    }

    @PostMapping
    public ResponseEntity<PaymentCardDTO> createCard(@Valid @RequestBody PaymentCardDTO cardDTO) {
        PaymentCardDTO createdCard = paymentCardDtoService.createCard(cardDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardDTO> getCardById(@PathVariable Long id) {
        Optional<PaymentCardDTO> card = paymentCardDtoService.getCardById(id);
        return card.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<PaymentCardDTO>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<PaymentCardDTO> cards = paymentCardDtoService.getAllCards(pageable);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentCardDTO>> getCardsByUserId(@PathVariable Long userId) {
        List<PaymentCardDTO> cards = paymentCardDtoService.getCardsByUserId(userId);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardDTO> updateCard(@PathVariable Long id, @Valid @RequestBody PaymentCardDTO cardDTO) {
        PaymentCardDTO updatedCard = paymentCardDtoService.updateCard(id, cardDTO);
        return ResponseEntity.ok(updatedCard);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateCard(@PathVariable Long id) {
        paymentCardDtoService.activateCard(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateCard(@PathVariable Long id) {
        paymentCardDtoService.deactivateCard(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        paymentCardDtoService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}