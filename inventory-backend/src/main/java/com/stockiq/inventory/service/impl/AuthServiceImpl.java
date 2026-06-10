package com.stockiq.inventory.service.impl;

import com.stockiq.inventory.common.enums.user.UserRole;
import com.stockiq.inventory.common.response.user.CreateUserResponse;
import com.stockiq.inventory.common.response.user.LoginResponse;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.common.util.JwtUtil;
import com.stockiq.inventory.dto.LoginDto;
import com.stockiq.inventory.dto.UserDto;
import com.stockiq.inventory.entity.User;
import com.stockiq.inventory.repository.UserRepository;
import com.stockiq.inventory.service.AuthService;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

  private static final String SERVICE_IMPL = "auth_service_impl";
  private static final String INIT_LOG = "init_log";
  private static final String EXCEPTION_LOG = "exception_log";

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public AuthServiceImpl(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  @Override
  public CreateUserResponse register(String correlationId, String logKey, UserDto userDto) {
    try {
      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      UserRole role = userDto.getRole() != null ? userDto.getRole() : UserRole.USER;

      User user =
          User.builder()
              .fullName(userDto.getFullName())
              .companyName(userDto.getCompanyName())
              .email(userDto.getEmail())
              .password(passwordEncoder.encode(userDto.getPassword()))
              .roleId(role.getValue())
              .createdBy(userDto.getEmail())
              .createdAt(Timestamp.valueOf(LocalDateTime.now()))
              .build();

      long id = userRepository.save(correlationId, logKey, user);

      return CreateUserResponse.builder()
          .id(id)
          .fullName(user.getFullName())
          .companyName(user.getCompanyName())
          .email(user.getEmail())
          .role(role)
          .build();

    } catch (DuplicateKeyException e) {
      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());
      throw new RuntimeException("Email already exists");
    } catch (Exception e) {
      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());
      throw new RuntimeException("Failed to register user");
    }
  }

  @Override
  public LoginResponse login(String correlationId, String logKey, LoginDto loginDto) {
    ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

    User user =
        userRepository
            .findByEmail(correlationId, logKey, loginDto.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

    if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
      throw new RuntimeException("Invalid email or password");
    }

    UserRole role = UserRole.fromValue(user.getRoleId());

    String token = jwtUtil.generateToken(user.getEmail(), role.name());

    return LoginResponse.builder()
        .token(token)
        .email(user.getEmail())
        .role(role)
        .build();
  }
}