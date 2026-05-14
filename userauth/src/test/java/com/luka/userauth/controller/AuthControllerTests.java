package com.luka.userauth.controller;

import com.luka.userauth.config.TestClockConfig;
import com.luka.userauth.dto.LoginDto;
import com.luka.userauth.dto.LoginResponseDtoController;
import com.luka.userauth.dto.LoginResponseDtoService;
import com.luka.userauth.dto.RegisterDto;
import com.luka.userauth.entity.RefreshToken;
import com.luka.userauth.entity.Role;
import com.luka.userauth.entity.User;
import com.luka.userauth.exception.exceptionclasses.RefreshTokenException;
import com.luka.userauth.exception.exceptionclasses.RegistrationFailedException;
import com.luka.userauth.security.SecurityConfig;
import com.luka.userauth.security.util.JWTUtil;
import com.luka.userauth.security.util.RefreshTokenUtil;
import com.luka.userauth.service.*;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import javax.security.auth.login.LoginException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Import({TestClockConfig.class, SecurityConfig.class})
@WebMvcTest(controllers = AuthController.class)
@ActiveProfiles("test")
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Clock clock;

    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private TokenService tokenService;
    @MockitoBean
    private VerificationService verificationService;
    @MockitoBean
    private LogoutService logoutService;
    @MockitoBean
    private RefreshTokenUtil refreshTokenUtil;
    @MockitoBean
    private RefreshTokenService refreshTokenService;
    @MockitoBean
    private JWTUtil jwtUtil;


    @Nested
    class RegisterTests{
        private RegisterDto registerDto;

        @ParameterizedTest
        @CsvFileSource(resources = "/mock_bad_register_requests.csv", useHeadersInDisplayName = true)
        void notValidRegisterRequestTest(@AggregateWith(RegisterDtoAggregator.class) RegisterDto regDto){

            try {
                mockMvc.perform(post("/auth/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(regDto)))
                        .andExpect(MockMvcResultMatchers.status().isBadRequest());
            } catch (Exception e) {
                System.out.println("Not valid register request test failed. Stacktrace: ");
                e.printStackTrace();
            }

        }

        @Test
        void registerFailServiceErrorTest(){

            registerDto = new RegisterDto("testName", "testSurname", "tesssst",
                    "test@ggmail.com", "ValidPassword12345@");

            Mockito.when(authService.register(Mockito.any(RegisterDto.class)))
                            .thenThrow(RegistrationFailedException.class);
            try {
                mockMvc.perform(post("/auth/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerDto)))
                        .andExpect(MockMvcResultMatchers.status().isBadRequest());

                Mockito.verify(authService).register(Mockito.any(RegisterDto.class));
            }catch (Exception e){
                System.out.println("Register fail service error test failed. Stacktrace: ");
                e.printStackTrace();
            }
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/mock_valid_register_requests.csv", useHeadersInDisplayName = true)
        void registerSuccessTest(@AggregateWith(RegisterDtoAggregator.class) RegisterDto regDto){

            String message = "Success.";

            Mockito.when(authService.register(regDto)).thenReturn(message);

            try {
                mockMvc.perform(post("/auth/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(regDto)))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers.content().contentType(message))
                        .andExpect(MockMvcResultMatchers.content().string(message));

                Mockito.verify(authService).register(regDto);
            } catch (Exception e) {
                System.out.println("Register success test failed. Stacktrace: ");
                e.printStackTrace();
            }
        }
    }

    @Nested
    class VerificationTests{

        private User user;
        private String token;
        private String message = "Email verification succecss.";
        private RefreshToken refreshToken;

        @BeforeEach
        void setup(){

            token = "SomeValidToken";

            user = new User(1L, "userNick1", "user1Name", "user1Surname", "user1@mail.com", "ValidPassword123@",
                    false, LocalDateTime.now(clock), null);

            refreshToken = new RefreshToken(1L, "refreshToken", false, LocalDateTime.now(clock),
                    LocalDateTime.now(clock).plusDays(1), user);
        }

        @Test
        void verificationFailInvalidToken(){

            Mockito.when(verificationService.verifyUser(Mockito.anyString()))
                    .thenThrow(RuntimeException.class);

            try {
                mockMvc.perform(get("/auth/validate-email")
                        .with(csrf())
                        .param("token", token))
                        .andExpect(MockMvcResultMatchers.status().isBadRequest());

                Mockito.verify(verificationService).verifyUser(Mockito.anyString());
            } catch (Exception e) {
                System.out.println("Verification fail invalid token test failed. Stacktrace: ");
                e.printStackTrace();
            }
        }

        @Test
        void verificationFailRefreshTokenCreationErrorTest(){
            Mockito.when(verificationService.verifyUser(token))
                    .thenReturn(user);

            Mockito.when(refreshTokenService.create(user))
                    .thenThrow(RefreshTokenException.class);

            try {
                mockMvc.perform(get("/auth/validate-email")
                        .param("token", token))
                        .andExpect(MockMvcResultMatchers.status().isBadRequest());

                Mockito.verify(verificationService).verifyUser(token);
                Mockito.verify(refreshTokenService).create(user);
            }catch (Exception e){
                System.out.println("Verification fail refresh token creation error test failed. Stacktrace: ");
                e.printStackTrace();
            }
        }

        @Test
        void verificationSuccessTest(){
            Mockito.when(verificationService.verifyUser(token))
                    .thenReturn(user);

            Mockito.when(refreshTokenService.create(user))
                    .thenReturn(refreshToken);

            try {
                mockMvc.perform(get("/auth/validate-email")
                        .param("token", token))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers
                                .content().string(message));

                Mockito.verify(verificationService.verifyUser(token));
                Mockito.verify(refreshTokenService).create(user);
            }catch (Exception e){
                System.out.println("Verification success test failed. Stacktrace: ");
                e.getStackTrace();
            }
        }
    }

    @Nested
    class LoginTests{
        private LoginDto loginDto;
        private User user;
        private String token;
        private RefreshToken refreshToken;
        private LoginResponseDtoService dtoRespService;
        private LoginResponseDtoController dtoRespController;

        @BeforeEach
        void setup(){
            token = "SomeValidToken";

            user = new User(1L, "userNick1", "user1Name", "user1Surname", "user1@mail.com", "ValidPassword123@",
                    false, LocalDateTime.now(clock), null);

            refreshToken = new RefreshToken(1L, "refreshToken", false, LocalDateTime.now(clock),
                    LocalDateTime.now(clock).plusDays(1), user);

            //dtoRespService = new LoginResponseDtoService()
        }


        @ParameterizedTest
        @CsvFileSource(resources = "/mock_bad_login_requests.csv", useHeadersInDisplayName = true)
        void loginFailInvalidLoginRequestsTest(@AggregateWith(LoginDtoAggregator.class) LoginDto loginDto){
            try {
                mockMvc.perform(post("auth/login")
                                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDto)))
                        .andExpect(MockMvcResultMatchers.status().isBadRequest());
            }catch (Exception e){
                System.out.println("Login fail invalid login requests test failed. Stacktrace: ");
                e.printStackTrace();
            }
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/mock_valid_login_requests.csv", useHeadersInDisplayName = true)
        void loginFailAuthenticationErrorTest(@AggregateWith(LoginDtoAggregator.class)LoginDto loginDto){
            Mockito.when(authService.login(loginDto))
                    .thenThrow(LoginException.class);

            try {
                mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDto)))
                        .andExpect(MockMvcResultMatchers.status().isBadRequest());

                Mockito.verify(authService.login(loginDto));
            } catch (Exception e) {
                System.out.println("Login fail authentication error test failed. Stacktrace: ");
                e.printStackTrace();
            }
        }

//        @ParameterizedTest
//        @CsvFileSource(resources = "/mock_valid_login_requests.csv", useHeadersInDisplayName = true)
//        void loginFailREfreshTokenErrorTest(@AggregateWith(LoginDtoAggregator.class)LoginDto loginDto){
//            Mockito.when(authService.login(loginDto))
//                    .thenReturn(Mockito.any(LoginResponseDtoService.class));
//
//        }

    }

}

class RegisterDtoAggregator implements ArgumentsAggregator {

    @Override
    public @Nullable Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
        return new RegisterDto(accessor.getString(0), accessor.getString(1), accessor.getString(2),
                accessor.getString(3), accessor.getString(4));
    }
}

class LoginDtoAggregator implements ArgumentsAggregator {

    @Override
    public @Nullable Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
        return new LoginDto(accessor.getString(0), accessor.getString(1));
    }
}