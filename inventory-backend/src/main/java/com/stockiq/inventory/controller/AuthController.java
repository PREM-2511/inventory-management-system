package com.stockiq.inventory.controller;

import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.response.user.CreateUserResponse;
import com.stockiq.inventory.common.response.user.LoginResponse;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.dto.LoginDto;
import com.stockiq.inventory.dto.UserDto;
import com.stockiq.inventory.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private static final String MAIN_LOG_KEY = "AUTH_";
  private static final String X_CORRELATION_ID = "X-Correlation-Id";
  private static final String INIT_LOG = "init_log";
  private static final String CONTROLLER = "controller";

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<CreateUserResponse> register(
      @RequestBody @Valid UserDto userDto,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId) {

    String logKey = MAIN_LOG_KEY + "REGISTER";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.CREATE.getValue()))
        .body(authService.register(correlationId, logKey, userDto));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
      @RequestBody @Valid LoginDto loginDto,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId) {

    String logKey = MAIN_LOG_KEY + "LOGIN";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(authService.login(correlationId, logKey, loginDto));
  }
}