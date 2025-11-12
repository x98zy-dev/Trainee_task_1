package org.x98zy.user_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.x98zy.user_service.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // Named method - найти по email
    Optional<User> findByEmail(String email);

    // Named method - найти всех активных пользователей
    List<User> findByActiveTrue();

    // JPQL - найти пользователей по имени и фамилии
    @Query("SELECT u FROM User u WHERE u.name = :name AND u.surname = :surname")
    List<User> findByNameAndSurname(@Param("name") String name, @Param("surname") String surname);

    // Native SQL - найти пользователей с картами
    @Query(value = "SELECT DISTINCT u.* FROM users u " +
            "JOIN payment_cards pc ON u.id = pc.user_id " +
            "WHERE pc.active = true",
            nativeQuery = true)
    List<User> findUsersWithActiveCards();

    // Пагинация с фильтрацией через Specification (будет в Service)
    Page<User> findAll(Pageable pageable);
}