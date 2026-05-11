package com.luka.userauth.service.impl;

import com.luka.userauth.config.TestClockConfig;
import com.luka.userauth.dto.RegisterDto;
import com.luka.userauth.entity.User;
import com.luka.userauth.exception.exceptionclasses.RegistrationFailedException;
import com.luka.userauth.exception.exceptionclasses.UserAlreadyExistsException;
import com.luka.userauth.repository.RoleRepository;
import com.luka.userauth.repository.UserRepository;
import com.luka.userauth.service.AuthService;
import com.luka.userauth.service.NotificationService;
import com.luka.userauth.service.TokenService;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Import(TestClockConfig.class)
@SpringBootTest
@ActiveProfiles("test")
public class AuthServiceImplTests {

    @Autowired
    private Clock clock;
    @Autowired
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private RoleRepository roleRepository;

    @MockitoBean
    private TokenService tokenService;
    @MockitoBean
    private NotificationService notificationService;

//    @Test
//    @CsvFileSource("FILEURL")
//    public void registerFailUserExistsTest(@AggregateWith(RegisterDtoAggregator.class) RegisterDto registerDto) {
//
//        Mockito.when(userRepository.findByEmail(Mockito.anyString()))
//                .thenReturn(null);
//
//    }
    @Nested
    class RegisterTests{

        private User user;
        private RegisterDto registerDto;

        @BeforeEach
        void setUp(){
            user = new User(null, "Player1", "NamePlayer1", "SurnamePlayer1",
                    "player1@gmail.com", "Player1!", false, LocalDateTime.now(clock).minusDays(1),
                    null);

            registerDto = new RegisterDto("a", "b", "c", "abc@example.com",
                    "Regularpassword1@");
        }

        @Test
        void registerFailUserExistsTest(){

            Mockito.when(userRepository.findByEmail(Mockito.anyString()))
                    .thenReturn(Optional.of(user));

            Assertions.assertThrows(UserAlreadyExistsException.class,()->{
                authService.register(registerDto);
            });

            Mockito.verify(userRepository).findByEmail(Mockito.anyString());

        }

        @Test
        void registerFailRoleErrorTest(){
            Mockito.when(userRepository.findByEmail(Mockito.anyString()))
                    .thenReturn(Optional.empty());

            Mockito.when(roleRepository.findByName(Mockito.anyString()))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(RegistrationFailedException.class,()->{
                authService.register(registerDto);
            });

            Mockito.verify(userRepository).findByEmail(Mockito.anyString());
            Mockito.verify(roleRepository).findByName(Mockito.anyString());
        }
    }


}

class RegisterDtoAggregator implements ArgumentsAggregator{

    @Override
    public @Nullable Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
        return new RegisterDto(accessor.getString(0), accessor.getString(1), accessor.getString(2),
                accessor.getString(3), accessor.getString(4));
    }
}