package com.stockiq.inventory.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.stockiq.inventory.common.enums.ErrorMessageCodeEnum;
import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.enums.user.UserRole;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.user.CreateUserResponse;
import com.stockiq.inventory.common.response.user.LoginResponse;
import com.stockiq.inventory.common.response.user.ProfileUpdateResponse;
import com.stockiq.inventory.common.util.ErrorMessage;
import com.stockiq.inventory.dto.LoginDto;
import com.stockiq.inventory.dto.OAuthLoginDto;
import com.stockiq.inventory.dto.ResetPasswordDto;
import com.stockiq.inventory.dto.UpdateProfileDto;
import com.stockiq.inventory.dto.UserDto;
import com.stockiq.inventory.service.AuthService;
import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Test")
class AuthControllerTest {

  private static final String X_CORRELATION_ID = "X-Correlation-Id";

  @Mock private AuthService authService;

  @InjectMocks private AuthController authController;

  private UserDto userDto;
  private LoginDto loginDto;
  private UpdateProfileDto updateProfileDto;

  private CreateUserResponse createUserResponse;
  private LoginResponse loginResponse;
  private ProfileUpdateResponse profileUpdateResponse;
  private ResetPasswordDto resetPasswordDto;
  private OAuthLoginDto oauthLoginDto;

  @BeforeEach
  void setUp() {

    userDto = new UserDto();
    userDto.setId(1L);
    userDto.setFullName("John Doe");
    userDto.setCompanyName("ABC Company");
    userDto.setEmail("john@test.com");
    userDto.setPassword("123456");
    userDto.setRole(UserRole.USER);

    loginDto = new LoginDto();
    loginDto.setEmail("john@test.com");
    loginDto.setPassword("123456");

    updateProfileDto = new UpdateProfileDto();
    updateProfileDto.setFullName("Updated User");

    createUserResponse =
        CreateUserResponse.builder()
            .id(1L)
            .fullName("John Doe")
            .companyName("ABC Company")
            .email("john@test.com")
            .role(UserRole.USER)
            .build();

    loginResponse =
        LoginResponse.builder()
            .token("jwt-token")
            .email("john@test.com")
            .role(UserRole.USER)
            .build();

    profileUpdateResponse =
        ProfileUpdateResponse.builder().token("updated-jwt-token").user(createUserResponse).build();

    resetPasswordDto = new ResetPasswordDto();

    oauthLoginDto = new OAuthLoginDto();
    oauthLoginDto.setAccessToken("access-token");

    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("register Valid User Returns Created Response")
  void register_ValidUser_ReturnsCreatedResponse() throws Exception {
    // Arrange
    when(authService.register(anyString(), anyString(), any(UserDto.class)))
        .thenReturn(createUserResponse);

    // Act
    ResponseEntity<CreateUserResponse> response =
        authController.register(userDto, X_CORRELATION_ID);

    // Assert
    assertNotNull(response);

    assertEquals(HttpStatusCodeEnum.CREATE.getValue(), response.getStatusCode().value());

    assertEquals(createUserResponse, response.getBody());

    // Verify
    verify(authService).register(anyString(), anyString(), any(UserDto.class));
  }

  @Test
  @DisplayName("register Service Throws GeneralStatusCodeErrorException")
  void register_ServiceThrowsGeneralStatusCodeErrorException() throws Exception {
    // Arrange
    GeneralStatusCodeErrorException exception = mock(GeneralStatusCodeErrorException.class);

    doThrow(exception).when(authService).register(anyString(), anyString(), any(UserDto.class));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> authController.register(userDto, X_CORRELATION_ID));

    // Verify
    verify(authService).register(anyString(), anyString(), any(UserDto.class));
  }

  @Test
  @DisplayName("login Valid Credentials Returns Login Response")
  void login_ValidCredentials_ReturnsLoginResponse() throws Exception {
    // Arrange
    when(authService.login(anyString(), anyString(), any(LoginDto.class)))
        .thenReturn(loginResponse);

    // Act
    ResponseEntity<LoginResponse> response = authController.login(loginDto, X_CORRELATION_ID);

    // Assert
    assertNotNull(response);

    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());

    assertEquals(loginResponse, response.getBody());

    // Verify
    verify(authService).login(anyString(), anyString(), any(LoginDto.class));
  }

  @Test
  @DisplayName("login Service Throws GeneralStatusCodeErrorException")
  void login_ServiceThrowsGeneralStatusCodeErrorException() throws Exception {
    // Arrange
    ErrorMessage errorMessage =
        new ErrorMessage(
            "Invalid email or password",
            "Login Failed",
            HttpStatus.UNAUTHORIZED,
            HttpStatusCodeEnum.UNAUTHORIZED.getValue(),
            ErrorMessageCodeEnum.LOGIN_FAILED.getCode(),
            new Date().toString(),
            null);

    when(authService.login(anyString(), anyString(), any(LoginDto.class)))
        .thenThrow(new GeneralStatusCodeErrorException(errorMessage));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> authController.login(loginDto, X_CORRELATION_ID));

    // Verify
    verify(authService).login(anyString(), anyString(), any(LoginDto.class));
  }

  @Test
  @DisplayName("getCurrentUser Authenticated User Returns User Details")
  void getCurrentUser_AuthenticatedUser_ReturnsUserDetails() throws Exception {
    // Arrange
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                "john@test.com", null, Collections.emptyList()));

    when(authService.getCurrentUser(anyString(), anyString(), anyString()))
        .thenReturn(createUserResponse);

    // Act
    ResponseEntity<CreateUserResponse> response = authController.getCurrentUser(X_CORRELATION_ID);

    // Assert
    assertNotNull(response);

    assertEquals(HttpStatusCodeEnum.OK.getValue(), response.getStatusCode().value());

    assertEquals(createUserResponse, response.getBody());

    // Verify
    verify(authService).getCurrentUser(anyString(), anyString(), eq("john@test.com"));
  }

  @Test
  @DisplayName("getCurrentUser No Authentication Returns Unauthorized")
  void getCurrentUser_NoAuthentication_ReturnsUnauthorized() throws Exception {
    // Arrange
    SecurityContextHolder.clearContext();

    // Act
    ResponseEntity<?> response = authController.getCurrentUser(X_CORRELATION_ID);

    // Assert
    assertNotNull(response);

    assertEquals(HttpStatusCodeEnum.UNAUTHORIZED.getValue(), response.getStatusCode().value());

    assertNull(response.getBody());

    // Verify
    verify(authService, never()).getCurrentUser(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("updateProfile Success")
  void updateProfile_Success() throws Exception {
    // Arrange
    Authentication authentication = mock(Authentication.class);
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    when(authentication.getPrincipal()).thenReturn("test@gmail.com");

    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);

    ProfileUpdateResponse profileUpdateResponse = new ProfileUpdateResponse();

    when(authService.updateProfile(
            anyString(), anyString(), anyString(), any(UpdateProfileDto.class)))
        .thenReturn(profileUpdateResponse);

    // Act
    ResponseEntity<?> response = authController.updateProfile(updateProfileDto, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(profileUpdateResponse, response.getBody());

    // Verify
    verify(authService)
        .updateProfile(anyString(), anyString(), eq("test@gmail.com"), any(UpdateProfileDto.class));

    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("updateProfile Unauthorized")
  void updateProfile_Unauthorized() throws Exception {
    // Arrange
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(null);
    SecurityContextHolder.setContext(context);

    // Act
    ResponseEntity<?> response = authController.updateProfile(updateProfileDto, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    // Verify
    verify(authService, never()).updateProfile(anyString(), anyString(), anyString(), any());
  }

  @Test
  @DisplayName("updateProfile Service Throws GeneralStatusCodeErrorException")
  void updateProfile_ServiceThrowsGeneralStatusCodeErrorException() throws Exception {
    // Arrange
    Authentication authentication = mock(Authentication.class);

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    when(authentication.getPrincipal()).thenReturn("test@gmail.com");
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);

    when(authService.updateProfile(
            anyString(), anyString(), anyString(), any(UpdateProfileDto.class)))
        .thenThrow(
            new GeneralStatusCodeErrorException(
                new ErrorMessage(
                    "Profile update failed",
                    "Update Failed",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
                    ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
                    new java.util.Date().toString(),
                    null)));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> authController.updateProfile(updateProfileDto, X_CORRELATION_ID));

    // Verify
    verify(authService)
        .updateProfile(anyString(), anyString(), anyString(), any(UpdateProfileDto.class));
  }

  @Test
  @DisplayName("forgotPassword Success")
  void forgotPassword_Success() throws Exception {
    // Arrange
    ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
    // set required fields if needed

    when(authService.resetPassword(anyString(), anyString(), any(ResetPasswordDto.class)))
        .thenReturn(ResponseEntity.ok().build().hasBody());

    // Act
    ResponseEntity<?> response = authController.forgotPassword(resetPasswordDto, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Verify
    verify(authService).resetPassword(anyString(), anyString(), any(ResetPasswordDto.class));
  }

  @Test
  @DisplayName("forgotPassword Service Throws GeneralStatusCodeErrorException")
  void forgotPassword_ServiceThrowsGeneralStatusCodeErrorException() throws Exception {
    // Arrange
    doThrow(
            new GeneralStatusCodeErrorException(
                new ErrorMessage(
                    "Reset failed",
                    "Forgot Password Failed",
                    HttpStatus.NOT_FOUND,
                    HttpStatusCodeEnum.NOT_FOUND.getValue(),
                    ErrorMessageCodeEnum.USER_NOT_FOUND.getCode(),
                    new java.util.Date().toString(),
                    null)))
        .when(authService)
        .resetPassword(anyString(), anyString(), any(ResetPasswordDto.class));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> authController.forgotPassword(resetPasswordDto, X_CORRELATION_ID));

    // Verify
    verify(authService).resetPassword(anyString(), anyString(), any(ResetPasswordDto.class));
  }

  @Test
  @DisplayName("loginWithGoogle Success Using AccessToken")
  void loginWithGoogle_Success() throws Exception {
    // Arrange
    oauthLoginDto.setAccessToken("access-token");

    when(authService.loginWithGoogle(anyString(), anyString(), eq("access-token")))
        .thenReturn(loginResponse);

    // Act
    ResponseEntity<?> response = authController.loginWithGoogle(oauthLoginDto, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());

    LoginResponse result = (LoginResponse) response.getBody();

    assertEquals("jwt-token", result.getToken());
    assertEquals("john@test.com", result.getEmail());
    assertEquals(UserRole.USER, result.getRole());

    // Verify
    verify(authService).loginWithGoogle(anyString(), anyString(), eq("access-token"));
  }

  @Test
  @DisplayName("loginWithGoogle Success Using IdToken")
  void loginWithGoogle_IdToken() throws Exception {
    // Arrange
    oauthLoginDto.setAccessToken(null);
    oauthLoginDto.setIdToken("id-token");

    when(authService.loginWithGoogle(anyString(), anyString(), eq("id-token")))
        .thenReturn(loginResponse);

    // Act
    ResponseEntity<?> response = authController.loginWithGoogle(oauthLoginDto, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());

    LoginResponse result = (LoginResponse) response.getBody();

    assertEquals("jwt-token", result.getToken());
    assertEquals("john@test.com", result.getEmail());
    assertEquals(UserRole.USER, result.getRole());

    // Verify
    verify(authService).loginWithGoogle(anyString(), anyString(), eq("id-token"));
  }

  @Test
  @DisplayName("loginWithGoogle Service Throws GeneralStatusCodeErrorException")
  void loginWithGoogle_ServiceThrowsGeneralStatusCodeErrorException() throws Exception {
    // Arrange
    oauthLoginDto.setAccessToken("access-token");

    when(authService.loginWithGoogle(anyString(), anyString(), eq("access-token")))
        .thenThrow(
            new GeneralStatusCodeErrorException(
                new ErrorMessage(
                    "Google Login Failed",
                    "OAuth Error",
                    HttpStatus.UNAUTHORIZED,
                    HttpStatusCodeEnum.UNAUTHORIZED.getValue(),
                    ErrorMessageCodeEnum.LOGIN_FAILED.getCode(),
                    new java.util.Date().toString(),
                    null)));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> authController.loginWithGoogle(oauthLoginDto, X_CORRELATION_ID));

    // Verify
    verify(authService).loginWithGoogle(anyString(), anyString(), eq("access-token"));
  }

  @Test
  @DisplayName("loginWithGithub Success")
  void loginWithGithub_Success() throws Exception {
    // Arrange
    oauthLoginDto.setCode("github-code");

    when(authService.loginWithGithub(anyString(), anyString(), eq("github-code")))
        .thenReturn(loginResponse);

    // Act
    ResponseEntity<?> response = authController.loginWithGithub(oauthLoginDto, X_CORRELATION_ID);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());

    LoginResponse result = (LoginResponse) response.getBody();

    assertEquals("jwt-token", result.getToken());
    assertEquals("john@test.com", result.getEmail());
    assertEquals(UserRole.USER, result.getRole());

    // Verify
    verify(authService).loginWithGithub(anyString(), anyString(), eq("github-code"));
  }

  @Test
  @DisplayName("loginWithGithub Service Throws GeneralStatusCodeErrorException")
  void loginWithGithub_ServiceThrowsGeneralStatusCodeErrorException() throws Exception {
    // Arrange
    oauthLoginDto.setCode("github-code");

    when(authService.loginWithGithub(anyString(), anyString(), eq("github-code")))
        .thenThrow(
            new GeneralStatusCodeErrorException(
                new ErrorMessage(
                    "Github Login Failed",
                    "OAuth Error",
                    HttpStatus.UNAUTHORIZED,
                    HttpStatusCodeEnum.UNAUTHORIZED.getValue(),
                    ErrorMessageCodeEnum.LOGIN_FAILED.getCode(),
                    new java.util.Date().toString(),
                    null)));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> authController.loginWithGithub(oauthLoginDto, X_CORRELATION_ID));

    // Verify
    verify(authService).loginWithGithub(anyString(), anyString(), eq("github-code"));
  }
}
