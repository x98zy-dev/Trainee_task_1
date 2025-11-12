package org.x98zy.user_service.mapper;

import org.mapstruct.*;
import org.x98zy.user_service.dto.PaymentCardDTO;
import org.x98zy.user_service.entity.PaymentCard;
import org.x98zy.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    @Mapping(target = "user", source = "userId", qualifiedByName = "userIdToUser")
    PaymentCard toEntity(PaymentCardDTO paymentCardDTO);

    @Mapping(target = "userId", source = "user.id")
    PaymentCardDTO toDTO(PaymentCard paymentCard);

    @Named("userIdToUser")
    default User userIdToUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}