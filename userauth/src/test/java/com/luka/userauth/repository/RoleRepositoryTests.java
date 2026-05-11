package com.luka.userauth.repository;

import com.luka.userauth.config.TestClockConfig;
import com.luka.userauth.entity.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@Import(TestClockConfig.class)
@SpringBootTest
@ActiveProfiles("test")
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void findByIdTest(){

        Optional<Role> role1 = roleRepository.findById(1L);

        Assertions.assertNotNull(role1);
        Assertions.assertEquals("ROLE_USER", role1.get().getName());
        Assertions.assertEquals(1, role1.get().getId().intValue());
    }

    @Test
    public void findByNameTest(){

        Optional<Role> role2 = roleRepository.findById(2L);

        Assertions.assertNotNull(role2);
        Assertions.assertEquals("ROLE_ADMIN", role2.get().getName());
        Assertions.assertEquals(2, role2.get().getId().intValue());

    }

}
