package org.x98zy.user_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.x98zy.user_service.entity.PaymentCard;
import java.util.List;
import java.util.Optional;

public interface PaymentCardService {
    PaymentCard createCard(PaymentCard card);
    Optional<PaymentCard> getCardById(Long id);
    Page<PaymentCard> getAllCards(Pageable pageable);
    List<PaymentCard> getCardsByUserId(Long userId);
    PaymentCard updateCard(Long id, PaymentCard cardDetails);
    void activateCard(Long id);
    void deactivateCard(Long id);
    void deleteCard(Long id);
    long countCardsByUserId(Long userId);
}