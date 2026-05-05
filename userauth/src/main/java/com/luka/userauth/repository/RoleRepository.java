package com.luka.userauth.repository;

import com.luka.userauth.entity.Role;
import com.luka.userauth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findById(Long id);

    Optional<Role> findByName(String name);

}
