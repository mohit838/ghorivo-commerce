package com.ghorivo.commerce.identity.controller;

import com.ghorivo.commerce.common.dto.ApiResponse;
import com.ghorivo.commerce.common.route.ApiRoutes;
import com.ghorivo.commerce.identity.dto.request.CreateUserRequestDto;
import com.ghorivo.commerce.identity.dto.response.UserResponseDto;
import com.ghorivo.commerce.identity.entity.UserAccount;
import com.ghorivo.commerce.identity.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRoutes.Identity.USERS)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> create(
            @Valid @RequestBody CreateUserRequestDto request
    ) {
        UserAccount createdUser = userService.create(request);

        UserResponseDto response =
                UserResponseDto.from(createdUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "User created successfully",
                        response
                ));
    }
}
