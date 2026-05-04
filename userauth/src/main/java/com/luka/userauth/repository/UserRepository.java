package com.luka.userauth.repository;

import com.luka.userauth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = :value OR u.nick = :value")
    Optional<User> findByEmailOrNick(@Param("value") String emailOrNick);

}
