package com.ghorivo.commerce.identity.service;

import com.ghorivo.commerce.identity.dto.request.CreateUserRequestDto;
import com.ghorivo.commerce.identity.entity.UserAccount;

public interface UserService {

    UserAccount create(CreateUserRequestDto request);
}
