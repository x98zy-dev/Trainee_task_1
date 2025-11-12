package org.x98zy.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.x98zy.user_service.dto.UserDTO;
import org.x98zy.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "paymentCards", ignore = true)
    User toEntity(UserDTO userDTO);

    UserDTO toDTO(User user);
}