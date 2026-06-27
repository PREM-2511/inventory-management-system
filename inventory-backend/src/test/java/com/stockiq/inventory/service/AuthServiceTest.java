package com.stockiq.inventory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockiq.inventory.common.enums.user.UserRole;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.user.CreateUserResponse;
import com.stockiq.inventory.common.response.user.LoginResponse;
import com.stockiq.inventory.common.response.user.ProfileUpdateResponse;
import com.stockiq.inventory.common.util.JwtUtil;
import com.stockiq.inventory.config.OAuthConfig;
import com.stockiq.inventory.dto.LoginDto;
import com.stockiq.inventory.dto.ResetPasswordDto;
import com.stockiq.inventory.dto.UpdateProfileDto;
import com.stockiq.inventory.dto.UserDto;
import com.stockiq.inventory.entity.User;
import com.stockiq.inventory.repository.UserRepository;
import com.stockiq.inventory.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  private static final String CORRELATION_ID = "corr-id";
  private static final String LOG_KEY = "log-key";

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtUtil jwtUtil;
  @Mock private OAuthConfig oauthConfig;
  @Mock private ObjectMapper objectMapper;
  @Mock private java.net.http.HttpClient httpClient;

  @InjectMocks private AuthServiceImpl authService;

  private User user;
  private UserDto userDto;
  private LoginDto loginDto;
  private UpdateProfileDto updateProfileDto;
  private ResetPasswordDto resetPasswordDto;

  @BeforeEach
  void setUp() {

    user =
        User.builder()
            .id(1L)
            .userId(1L)
            .fullName("Test User")
            .email("test@mail.com")
            .password("encoded-pass")
            .roleId(UserRole.USER.getValue())
            .build();

    userDto = new UserDto();
    userDto.setFullName("Test User");
    userDto.setEmail("test@mail.com");
    userDto.setPassword("1234");

    loginDto = new LoginDto();
    loginDto.setEmail("test@mail.com");
    loginDto.setPassword("1234");

    updateProfileDto = new UpdateProfileDto();
    updateProfileDto.setFullName("Updated Name");
    updateProfileDto.setEmail("updated@mail.com");
    updateProfileDto.setCompanyName("ABC");

    resetPasswordDto = new ResetPasswordDto();
    resetPasswordDto.setEmail("test@mail.com");
    resetPasswordDto.setNewPassword("new-pass");
  }

  @Test
  @DisplayName("register success")
  void register_ReturnsSuccess() throws Exception {
    // Arrange
    when(passwordEncoder.encode(anyString())).thenReturn("encoded-pass");
    when(userRepository.save(anyString(), anyString(), any(User.class))).thenReturn(1L);
    when(userRepository.update(anyString(), anyString(), any(User.class))).thenReturn(1);

    // Act
    CreateUserResponse response = authService.register(CORRELATION_ID, LOG_KEY, userDto);

    // Assert
    assertNotNull(response);
    assertEquals(1L, response.getId());

    // Verify
    verify(userRepository, times(1)).save(anyString(), anyString(), any(User.class));
  }

  @Test
  @DisplayName("register duplicate email throws exception")
  void register_Duplicate() throws Exception {
    // Arrange
    when(passwordEncoder.encode(anyString())).thenReturn("encoded-pass");
    when(userRepository.save(anyString(), anyString(), any(User.class)))
        .thenThrow(new DuplicateKeyException("duplicate"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> authService.register(CORRELATION_ID, LOG_KEY, userDto));

    // Verify
    verify(userRepository, times(1)).save(anyString(), anyString(), any(User.class));
  }

  @Test
  @DisplayName("register should throw GeneralStatusCodeErrorException when unexpected error occurs")
  void register_shouldThrowException_whenUnexpectedErrorOccurs() throws Exception {
    // Arrange
    when(passwordEncoder.encode(anyString())).thenReturn("encoded-pass");

    when(userRepository.save(anyString(), anyString(), any(User.class)))
        .thenThrow(new RuntimeException("Database failure"));

    // Act & Assert
    GeneralStatusCodeErrorException exception =
        assertThrows(
            GeneralStatusCodeErrorException.class,
            () -> authService.register(CORRELATION_ID, LOG_KEY, userDto));

    assertNotNull(exception);

    // Verify interaction
    verify(userRepository, times(1)).save(anyString(), anyString(), any(User.class));
  }

  @Test
  @DisplayName("login success returns token")
  void login_ReturnsSuccess() throws Exception {
    // Arrange
    when(userRepository.findByEmail(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(user));

    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("jwt-token");

    // Act
    LoginResponse response = authService.login(CORRELATION_ID, LOG_KEY, loginDto);

    // Assert
    assertNotNull(response);
    assertEquals("jwt-token", response.getToken());

    // Verify
    verify(jwtUtil, times(1)).generateToken(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("login user not found throws exception")
  void login_UserNotFound_ThrowsException() throws Exception {
    // Arrange
    when(userRepository.findByEmail(anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> authService.login(CORRELATION_ID, LOG_KEY, loginDto));
  }

  @Test
  @DisplayName("login null password throws exception")
  void login_NullPassword_ThrowsException() throws Exception {
    // Arrange
    user.setPassword(null);

    when(userRepository.findByEmail(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(user));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> authService.login(CORRELATION_ID, LOG_KEY, loginDto));
  }

  @Test
  @DisplayName("login invalid password throws exception")
  void login_InvalidPassword_ThrowsException() throws Exception {
    // Arrange
    when(userRepository.findByEmail(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(user));

    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> authService.login(CORRELATION_ID, LOG_KEY, loginDto));
  }

  @Test
  @DisplayName("login unexpected exception throws internal server error")
  void login_UnexpectedException_ThrowsInternalError() throws Exception {
    // Arrange
    when(userRepository.findByEmail(anyString(), anyString(), anyString()))
        .thenThrow(new RuntimeException("DB failure"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> authService.login(CORRELATION_ID, LOG_KEY, loginDto));
  }

  @Test
  @DisplayName("get current user success")
  void getCurrentUser_ReturnsSuccess() throws Exception {
    // Arrange
    when(userRepository.findByEmail(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(user));

    // Act
    CreateUserResponse response =
        authService.getCurrentUser(CORRELATION_ID, LOG_KEY, user.getEmail());

    // Assert
    assertNotNull(response);
    assertEquals(user.getEmail(), response.getEmail());

    // Verify
    verify(userRepository, times(1)).findByEmail(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("get current user not found throws exception")
  void getCurrentUser_UserNotFound_ThrowsException() throws Exception {
    // Arrange
    when(userRepository.findByEmail(anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> authService.getCurrentUser(CORRELATION_ID, LOG_KEY, "missing@mail.com"));
  }

  @Test
  @DisplayName("get current user unexpected exception throws internal error")
  void getCurrentUser_UnexpectedException_ThrowsInternalError() throws Exception {

    // Arrange
    when(userRepository.findByEmail(anyString(), anyString(), anyString()))
        .thenThrow(new RuntimeException("DB failure"));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () -> authService.getCurrentUser(CORRELATION_ID, LOG_KEY, "test@mail.com"));
  }

  @Test
  @DisplayName("update profile success")
  void updateProfile_ReturnsSuccess() throws Exception {
    // Arrange
    when(userRepository.findByEmail(anyString(), anyString(), eq(user.getEmail())))
        .thenReturn(Optional.of(user));

    when(userRepository.findByEmail(anyString(), anyString(), eq(updateProfileDto.getEmail())))
        .thenReturn(Optional.empty());

    when(userRepository.update(anyString(), anyString(), any(User.class))).thenReturn(1);

    when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("new-token");

    // Act
    ProfileUpdateResponse response =
        authService.updateProfile(CORRELATION_ID, LOG_KEY, user.getEmail(), updateProfileDto);

    // Assert
    assertNotNull(response);

    // Verify
    verify(userRepository, times(1)).update(anyString(), anyString(), any(User.class));
  }

  @Test
  @DisplayName("update profile user not found covers orElseThrow")
  void updateProfile_UserNotFound_CoversOrElseThrow() throws Exception {
    // Arrange
    when(userRepository.findByEmail(anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () ->
            authService.updateProfile(
                CORRELATION_ID, LOG_KEY, "missing@mail.com", updateProfileDto));
  }

  @Test
  @DisplayName("update profile duplicate email covers conflict branch")
  void updateProfile_DuplicateEmail_CoversConflictBranch() throws Exception {
    // Arrange
    when(userRepository.findByEmail(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(user));

    when(userRepository.findByEmail(anyString(), anyString(), eq(updateProfileDto.getEmail())))
        .thenReturn(Optional.of(new User()));

    // Act & Assert
    assertThrows(
        GeneralStatusCodeErrorException.class,
        () ->
            authService.updateProfile(CORRELATION_ID, LOG_KEY, user.getEmail(), updateProfileDto));
  }

  @Test
  @DisplayName("update profile covers generic exception catch block")
  void updateProfile_CoversGenericExceptionCatch() throws Exception {
    // Arrange
    when(userRepository.findByEmail(anyString(), anyString(), anyString()))
        .thenThrow(new RuntimeException("DB failure"));

    // Act & Assert
    GeneralStatusCodeErrorException exception =
        assertThrows(
            GeneralStatusCodeErrorException.class,
            () ->
                authService.updateProfile(
                    CORRELATION_ID, LOG_KEY, "test@mail.com", updateProfileDto));

    assertNotNull(exception);

    // Verify repository interaction happened
    verify(userRepository, times(1)).findByEmail(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("reset password success")
  void resetPassword_ReturnsTrue() throws Exception {
    // Arrange
    when(userRepository.findByEmail(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(user));

    when(passwordEncoder.encode(anyString())).thenReturn("encoded-new");
    when(userRepository.update(anyString(), anyString(), any(User.class))).thenReturn(1);

    // Act
    boolean result = authService.resetPassword(CORRELATION_ID, LOG_KEY, resetPasswordDto);

    // Assert
    assertTrue(result);

    // Verify
    verify(userRepository, times(1)).update(anyString(), anyString(), any(User.class));
  }

  @Test
  @DisplayName("reset password user not found covers orElseThrow")
  void resetPassword_UserNotFound_CoversOrElseThrow() throws Exception {

    when(userRepository.findByEmail(anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());

    GeneralStatusCodeErrorException ex =
        assertThrows(
            GeneralStatusCodeErrorException.class,
            () -> authService.resetPassword(CORRELATION_ID, LOG_KEY, resetPasswordDto));

    assertNotNull(ex);

    verify(userRepository, times(1)).findByEmail(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("reset password covers generic exception catch block")
  void resetPassword_CoversGenericExceptionCatch() throws Exception {
    // Arrange
    when(userRepository.findByEmail(anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(user));

    // FORCE exception AFTER find
    when(passwordEncoder.encode(anyString())).thenThrow(new RuntimeException("forced failure"));

    // Act & Assert
    GeneralStatusCodeErrorException ex =
        assertThrows(
            GeneralStatusCodeErrorException.class,
            () -> authService.resetPassword(CORRELATION_ID, LOG_KEY, resetPasswordDto));

    assertNotNull(ex);

    verify(passwordEncoder, times(1)).encode(anyString());
  }
}
