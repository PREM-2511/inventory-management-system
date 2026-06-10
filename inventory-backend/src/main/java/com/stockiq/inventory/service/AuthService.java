package com.stockiq.inventory.service;

import com.stockiq.inventory.common.response.user.CreateUserResponse;
import com.stockiq.inventory.common.response.user.LoginResponse;
import com.stockiq.inventory.dto.LoginDto;
import com.stockiq.inventory.dto.UserDto;

public interface AuthService {

  CreateUserResponse register(String correlationId, String logKey, UserDto userDto);

  LoginResponse login(String correlationId, String logKey, LoginDto loginDto);
}