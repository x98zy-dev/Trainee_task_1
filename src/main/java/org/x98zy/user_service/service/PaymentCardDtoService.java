package org.x98zy.user_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.x98zy.user_service.dto.PaymentCardDTO;
import java.util.List;
import java.util.Optional;

public interface PaymentCardDtoService {
    PaymentCardDTO createCard(PaymentCardDTO cardDTO);
    Optional<PaymentCardDTO> getCardById(Long id);
    Page<PaymentCardDTO> getAllCards(Pageable pageable);
    List<PaymentCardDTO> getCardsByUserId(Long userId);
    PaymentCardDTO updateCard(Long id, PaymentCardDTO cardDTO);
    void activateCard(Long id);
    void deactivateCard(Long id);
    void deleteCard(Long id);
}