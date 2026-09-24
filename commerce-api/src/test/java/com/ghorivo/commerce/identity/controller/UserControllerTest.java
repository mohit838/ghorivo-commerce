package com.ghorivo.commerce.identity.controller;

import com.ghorivo.commerce.common.exception.GlobalExceptionHandler;
import com.ghorivo.commerce.common.route.ApiRoutes;
import com.ghorivo.commerce.identity.constants.UserRole;
import com.ghorivo.commerce.identity.dto.request.CreateUserRequestDto;
import com.ghorivo.commerce.identity.entity.UserAccount;
import com.ghorivo.commerce.identity.exception.DuplicateUserEmailException;
import com.ghorivo.commerce.identity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    private static final Instant FIXED_TIME =
            Instant.parse("2026-09-24T10:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private Clock clock;

    @BeforeEach
    void setUp() {
        when(clock.instant()).thenReturn(FIXED_TIME);
    }

    @Test
    void shouldCreateUser() throws Exception {
        UserAccount createdUser = UserAccount.create(
                "First Admin",
                "admin@ghorivo.com",
                "hashed-password",
                UserRole.ADMIN,
                FIXED_TIME
        );

        when(userService.create(any(CreateUserRequestDto.class)))
                .thenReturn(createdUser);

        mockMvc.perform(post(ApiRoutes.Identity.USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "First Admin",
                                  "email": "admin@ghorivo.com",
                                  "rawPassword": "ChangeMe123!",
                                  "role": "ADMIN"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("User created successfully"))
                .andExpect(jsonPath("$.data.fullName")
                        .value("First Admin"))
                .andExpect(jsonPath("$.data.email")
                        .value("admin@ghorivo.com"))
                .andExpect(jsonPath("$.data.role")
                        .value("ADMIN"))
                .andExpect(jsonPath("$.data.status")
                        .value("ACTIVE"))
                .andExpect(jsonPath("$.data.createdAt")
                        .value("2026-09-24T10:00:00Z"))
                .andExpect(jsonPath("$.data.updatedAt")
                        .value("2026-09-24T10:00:00Z"));

        verify(userService)
                .create(any(CreateUserRequestDto.class));
    }

    @Test
    void shouldReturnValidationErrors() throws Exception {
        mockMvc.perform(post(ApiRoutes.Identity.USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "",
                                  "email": "invalid-email",
                                  "rawPassword": "123",
                                  "role": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.fullName")
                        .value("Full name is required"))
                .andExpect(jsonPath("$.fieldErrors.email")
                        .value("Email format is invalid"))
                .andExpect(jsonPath("$.fieldErrors.rawPassword")
                        .value(
                                "Password must contain 8 to 64 characters"
                        ))
                .andExpect(jsonPath("$.fieldErrors.role")
                        .value("Role is required"));

        verify(userService, never())
                .create(any(CreateUserRequestDto.class));
    }

    @Test
    void shouldReturnConflictForDuplicateEmail() throws Exception {
        when(userService.create(any(CreateUserRequestDto.class)))
                .thenThrow(new DuplicateUserEmailException(
                        "admin@ghorivo.com"
                ));

        mockMvc.perform(post(ApiRoutes.Identity.USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "First Admin",
                                  "email": "admin@ghorivo.com",
                                  "rawPassword": "ChangeMe123!",
                                  "role": "ADMIN"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code")
                        .value("DUPLICATE_USER_EMAIL"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "User already exists with email: "
                                        + "admin@ghorivo.com"
                        ))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/users"));
    }
}
