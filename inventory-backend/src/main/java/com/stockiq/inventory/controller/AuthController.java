package com.stockiq.inventory.controller;

import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.user.CreateUserResponse;
import com.stockiq.inventory.common.response.user.LoginResponse;
import com.stockiq.inventory.common.response.user.ProfileUpdateResponse;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.dto.LoginDto;
import com.stockiq.inventory.dto.UserDto;
import com.stockiq.inventory.dto.UpdateProfileDto;
import com.stockiq.inventory.dto.ResetPasswordDto;
import com.stockiq.inventory.dto.OAuthLoginDto;
import com.stockiq.inventory.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

  /**
   * Register a new user.
   *
   * @param userDto UserDto
   * @param correlationId String
   * @return ResponseEntity<CreateUserResponse>
   */
  @PostMapping("/register")
  public ResponseEntity<CreateUserResponse> register(
      @RequestBody @Valid UserDto userDto,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "REGISTER";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.CREATE.getValue()))
        .body(authService.register(correlationId, logKey, userDto));
  }

  /**
   * Authenticate user and generate JWT token.
   *
   * @param loginDto LoginDto
   * @param correlationId String
   * @return ResponseEntity<LoginResponse>
   */
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
      @RequestBody @Valid LoginDto loginDto,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "LOGIN";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(authService.login(correlationId, logKey, loginDto));
  }

  /**
   * Get current authenticated user profile from JWT token.
   *
   * @param correlationId String
   * @return ResponseEntity<CreateUserResponse>
   */
  @GetMapping("/me")
  public ResponseEntity<CreateUserResponse> getCurrentUser(
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "ME";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email =
        (auth != null && auth.getPrincipal() != null) ? auth.getPrincipal().toString() : null;

    if (email == null) {
      return ResponseEntity.status(
              HttpStatusCode.valueOf(HttpStatusCodeEnum.UNAUTHORIZED.getValue()))
          .build();
    }

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(authService.getCurrentUser(correlationId, logKey, email));
  }

  /**
   * Update the profile details of the currently authenticated user.
   *
   * @param updateDto UpdateProfileDto
   * @param correlationId String
   * @return ResponseEntity<ProfileUpdateResponse>
   */
  @PutMapping("/me")
  public ResponseEntity<ProfileUpdateResponse> updateProfile(
      @RequestBody @Valid UpdateProfileDto updateDto,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "UPDATE_PROFILE";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail =
        (auth != null && auth.getPrincipal() != null) ? auth.getPrincipal().toString() : null;

    if (currentUserEmail == null) {
      return ResponseEntity.status(
              HttpStatusCode.valueOf(HttpStatusCodeEnum.UNAUTHORIZED.getValue()))
          .build();
    }

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
        .body(authService.updateProfile(correlationId, logKey, currentUserEmail, updateDto));
  }

  /**
   * Reset the password of a user by email.
   *
   * @param resetDto ResetPasswordDto
   * @param correlationId String
   * @return ResponseEntity<Void>
   */
  @PostMapping("/forgot-password")
  public ResponseEntity<Void> forgotPassword(
      @RequestBody @Valid ResetPasswordDto resetDto,
      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
      throws GeneralStatusCodeErrorException {

    String logKey = MAIN_LOG_KEY + "FORGOT_PASSWORD";

    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);

    authService.resetPassword(correlationId, logKey, resetDto);

    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue())).build();
  }

//  /**
//   * Authenticate a user using Google OAuth.
//   *
//   * @param oauthDto OAuthLoginDto
//   * @param correlationId String
//   * @return ResponseEntity<LoginResponse>
//   */
//  @PostMapping("/oauth/google")
//  public ResponseEntity<LoginResponse> loginWithGoogle(
//      @RequestBody OAuthLoginDto oauthDto,
//      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
//      throws GeneralStatusCodeErrorException {
//
//    String logKey = MAIN_LOG_KEY + "OAUTH_GOOGLE";
//
//    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);
//
//    String token =
//        oauthDto.getAccessToken() != null ? oauthDto.getAccessToken() : oauthDto.getIdToken();
//    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
//        .body(authService.loginWithGoogle(correlationId, logKey, token));
//  }
//
//  /**
//   * Authenticate a user using GitHub OAuth.
//   *
//   * @param oauthDto OAuthLoginDto
//   * @param correlationId String
//   * @return ResponseEntity<LoginResponse>
//   */
//  @PostMapping("/oauth/github")
//  public ResponseEntity<LoginResponse> loginWithGithub(
//      @RequestBody OAuthLoginDto oauthDto,
//      @RequestHeader(value = X_CORRELATION_ID, defaultValue = "") String correlationId)
//      throws GeneralStatusCodeErrorException {
//
//    String logKey = MAIN_LOG_KEY + "OAUTH_GITHUB";
//
//    ActivityLog.logInfo(correlationId, logKey, CONTROLLER, INIT_LOG);
//
//    return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatusCodeEnum.OK.getValue()))
//        .body(authService.loginWithGithub(correlationId, logKey, oauthDto.getCode()));
//  }
}
