package org.x98zy.user_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.x98zy.user_service.entity.PaymentCard;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

    List<PaymentCard> findByUserId(Long userId);

    List<PaymentCard> findByUserIdAndActiveTrue(Long userId);

    Optional<PaymentCard> findByNumber(String number);

    @Modifying
    @Query("UPDATE PaymentCard p SET p.active = :active WHERE p.id = :id")
    void updateCardStatus(@Param("id") Long id, @Param("active") Boolean active);

    @Query(value = "SELECT * FROM payment_cards WHERE holder ILIKE %:holderName%",
            nativeQuery = true)
    List<PaymentCard> findByHolderName(@Param("holderName") String holderName);

    Page<PaymentCard> findAll(Pageable pageable);

    long countByUserId(Long userId);
}