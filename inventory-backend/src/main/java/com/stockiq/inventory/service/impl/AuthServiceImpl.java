package com.stockiq.inventory.service.impl;

import com.stockiq.inventory.common.enums.ErrorMessageCodeEnum;
import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.enums.user.UserRole;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.user.CreateUserResponse;
import com.stockiq.inventory.common.response.user.LoginResponse;
import com.stockiq.inventory.common.response.user.ProfileUpdateResponse;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.common.util.ApiError;
import com.stockiq.inventory.common.util.ErrorMessage;
import com.stockiq.inventory.common.util.JwtUtil;
import com.stockiq.inventory.config.OAuthConfig;
import com.stockiq.inventory.dto.LoginDto;
import com.stockiq.inventory.dto.UserDto;
import com.stockiq.inventory.dto.UpdateProfileDto;
import com.stockiq.inventory.dto.ResetPasswordDto;
import com.stockiq.inventory.entity.User;
import com.stockiq.inventory.repository.UserRepository;
import com.stockiq.inventory.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

  private static final String SERVICE_IMPL = "auth_service_impl";
  private static final String INIT_LOG = "init_log";
  private static final String EXCEPTION_LOG = "exception_log";

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final OAuthConfig oauthConfig;
  private final ObjectMapper objectMapper;
  private final HttpClient httpClient;

  public AuthServiceImpl(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtUtil jwtUtil,
      OAuthConfig oauthConfig,
      ObjectMapper objectMapper) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.oauthConfig = oauthConfig;
    this.objectMapper = objectMapper;
    this.httpClient = HttpClient.newBuilder().build();
  }

  /**
   * Register User
   *
   * @param correlationId String
   * @param logKey String
   * @param userDto UserDto
   * @return CreateUserResponse
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public CreateUserResponse register(String correlationId, String logKey, UserDto userDto)
      throws GeneralStatusCodeErrorException {

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
              .createdAt(Timestamp.valueOf(LocalDateTime.now()))
              .build();

      long id = userRepository.save(correlationId, logKey, user);

      // Set user_id and created_by to the user's own numeric ID after insert
      user.setId(id);
      user.setUserId(id);
      user.setCreatedBy(String.valueOf(id));
      user.setUpdatedAt(null);
      userRepository.update(correlationId, logKey, user);

      return CreateUserResponse.builder()
          .id(id)
          .fullName(user.getFullName())
          .companyName(user.getCompanyName())
          .email(user.getEmail())
          .role(role)
          .build();

    } catch (DuplicateKeyException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "User with email " + userDto.getEmail() + " already exists",
              "Duplicate User",
              HttpStatus.CONFLICT,
              HttpStatusCodeEnum.CONFLICT.getValue(),
              ErrorMessageCodeEnum.USER_DUPLICATE.getCode(),
              new java.util.Date().toString(),
              null));

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(new ErrorMessage(ApiError.INTERNAL_SERVER_ERROR));
    }
  }

  /**
   * Login User
   *
   * @param correlationId String
   * @param logKey String
   * @param loginDto LoginDto
   * @return LoginResponse
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public LoginResponse login(String correlationId, String logKey, LoginDto loginDto)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      User user =
          userRepository
              .findByEmail(correlationId, logKey, loginDto.getEmail())
              .orElseThrow(
                  () ->
                      new GeneralStatusCodeErrorException(
                          new ErrorMessage(
                              "Invalid email or password",
                              "Login Failed",
                              HttpStatus.UNAUTHORIZED,
                              HttpStatusCodeEnum.UNAUTHORIZED.getValue(),
                              ErrorMessageCodeEnum.LOGIN_FAILED.getCode(),
                              new java.util.Date().toString(),
                              null)));

      if (user.getPassword() == null) {
        throw new GeneralStatusCodeErrorException(
            new ErrorMessage(
                "This account uses Google or GitHub login",
                "Login Failed",
                HttpStatus.BAD_REQUEST,
                HttpStatusCodeEnum.BAD_REQUEST.getValue(),
                ErrorMessageCodeEnum.LOGIN_FAILED.getCode(),
                new java.util.Date().toString(),
                null));
      }

      if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {

        throw new GeneralStatusCodeErrorException(
            new ErrorMessage(
                "Invalid email or password",
                "Login Failed",
                HttpStatus.UNAUTHORIZED,
                HttpStatusCodeEnum.UNAUTHORIZED.getValue(),
                ErrorMessageCodeEnum.LOGIN_FAILED.getCode(),
                new java.util.Date().toString(),
                null));
      }

      UserRole role = UserRole.fromValue(user.getRoleId());

      String token = jwtUtil.generateToken(user.getEmail(), role.name(), user.getId());

      return LoginResponse.builder().token(token).email(user.getEmail()).role(role).build();

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(new ErrorMessage(ApiError.INTERNAL_SERVER_ERROR));
    }
  }

  /**
   * Get Current User
   *
   * @param correlationId String
   * @param logKey String
   * @param email String
   * @return CreateUserResponse
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public CreateUserResponse getCurrentUser(String correlationId, String logKey, String email)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      User user =
          userRepository
              .findByEmail(correlationId, logKey, email)
              .orElseThrow(
                  () ->
                      new GeneralStatusCodeErrorException(
                          new ErrorMessage(
                              "User not found",
                              "User Not Found",
                              HttpStatus.NOT_FOUND,
                              HttpStatusCodeEnum.NOT_FOUND.getValue(),
                              ErrorMessageCodeEnum.USER_NOT_FOUND.getCode(),
                              new java.util.Date().toString(),
                              null)));

      UserRole role = UserRole.fromValue(user.getRoleId());

      return CreateUserResponse.builder()
          .id(user.getId())
          .fullName(user.getFullName())
          .companyName(user.getCompanyName())
          .email(user.getEmail())
          .role(role)
          .build();

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(new ErrorMessage(ApiError.INTERNAL_SERVER_ERROR));
    }
  }

  /**
   * Update User Profile
   *
   * @param correlationId String
   * @param logKey String
   * @param currentUserEmail String
   * @param updateDto UpdateProfileDto
   * @return ProfileUpdateResponse
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public ProfileUpdateResponse updateProfile(
      String correlationId, String logKey, String currentUserEmail, UpdateProfileDto updateDto)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      User user =
          userRepository
              .findByEmail(correlationId, logKey, currentUserEmail)
              .orElseThrow(
                  () ->
                      new GeneralStatusCodeErrorException(
                          new ErrorMessage(
                              "User not found",
                              "User Not Found",
                              HttpStatus.NOT_FOUND,
                              HttpStatusCodeEnum.NOT_FOUND.getValue(),
                              ErrorMessageCodeEnum.USER_NOT_FOUND.getCode(),
                              new java.util.Date().toString(),
                              null)));

      if (!user.getEmail().equalsIgnoreCase(updateDto.getEmail())
          && userRepository.findByEmail(correlationId, logKey, updateDto.getEmail()).isPresent()) {

        throw new GeneralStatusCodeErrorException(
            new ErrorMessage(
                "Email already exists",
                "Duplicate User",
                HttpStatus.CONFLICT,
                HttpStatusCodeEnum.CONFLICT.getValue(),
                ErrorMessageCodeEnum.USER_DUPLICATE.getCode(),
                new java.util.Date().toString(),
                null));
      }

      user.setFullName(updateDto.getFullName());
      user.setCompanyName(updateDto.getCompanyName());
      user.setEmail(updateDto.getEmail());
      user.setUpdatedBy(currentUserEmail);
      user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

      userRepository.update(correlationId, logKey, user);

      UserRole role = UserRole.fromValue(user.getRoleId());

      return ProfileUpdateResponse.builder()
          .token(jwtUtil.generateToken(user.getEmail(), role.name(), user.getId()))
          .user(
              CreateUserResponse.builder()
                  .id(user.getId())
                  .fullName(user.getFullName())
                  .companyName(user.getCompanyName())
                  .email(user.getEmail())
                  .role(role)
                  .build())
          .build();

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(new ErrorMessage(ApiError.INTERNAL_SERVER_ERROR));
    }
  }

  /**
   * Reset Password
   *
   * @param correlationId String
   * @param logKey String
   * @param resetDto
   * @return boolean
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public boolean resetPassword(String correlationId, String logKey, ResetPasswordDto resetDto)
      throws GeneralStatusCodeErrorException {
    try {
      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);
      User user =
          userRepository
              .findByEmail(correlationId, logKey, resetDto.getEmail())
              .orElseThrow(
                  () ->
                      new GeneralStatusCodeErrorException(
                          new ErrorMessage(
                              "User not found",
                              "User Not Found",
                              HttpStatus.NOT_FOUND,
                              HttpStatusCodeEnum.NOT_FOUND.getValue(),
                              ErrorMessageCodeEnum.USER_NOT_FOUND.getCode(),
                              new java.util.Date().toString(),
                              null)));
      user.setPassword(passwordEncoder.encode(resetDto.getNewPassword()));
      user.setUpdatedBy(resetDto.getEmail());
      user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
      int update = userRepository.update(correlationId, logKey, user);
      return update > 0;
    } catch (GeneralStatusCodeErrorException e) {
      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());
      throw e;
    } catch (Exception e) {
      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());
      throw new GeneralStatusCodeErrorException(new ErrorMessage(ApiError.INTERNAL_SERVER_ERROR));
    }
  }

  //  /**
  //   * Login With Google
  //   *
  //   * @param correlationId String
  //   * @param logKey String
  //   * @param tokenStr String
  //   * @return LoginResponse
  //   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
  //   */
  //  @Override
  //  public LoginResponse loginWithGoogle(String correlationId, String logKey, String tokenStr)
  //      throws GeneralStatusCodeErrorException {
  //
  //    try {
  //
  //      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);
  //
  //      String email = null;
  //      String name = null;
  //
  //      // Determine whether the token is an ID token or an access token
  //      if (tokenStr.startsWith("ey")) {
  //
  //        // Verify Google ID token using Google API
  //        GoogleIdTokenVerifier verifier =
  //            new GoogleIdTokenVerifier.Builder(
  //                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
  //                .setAudience(Collections.singletonList(oauthConfig.getGoogle().getClientId()))
  //                .build();
  //
  //        GoogleIdToken idToken = verifier.verify(tokenStr);
  //
  //        if (idToken == null) {
  //
  //          throw new GeneralStatusCodeErrorException(
  //              new ErrorMessage(
  //                  "Invalid Google ID Token",
  //                  "Google Login Failed",
  //                  HttpStatus.UNAUTHORIZED,
  //                  HttpStatusCodeEnum.UNAUTHORIZED.getValue(),
  //                  ErrorMessageCodeEnum.LOGIN_FAILED.getCode(),
  //                  new java.util.Date().toString(),
  //                  null));
  //        }
  //
  //        // Extract user details from token payload
  //        GoogleIdToken.Payload payload = idToken.getPayload();
  //
  //        email = payload.getEmail();
  //        name = (String) payload.get("name");
  //
  //        // Use given name if name is unavailable
  //        if (name == null || name.trim().isEmpty()) {
  //          name = (String) payload.get("given_name");
  //        }
  //
  //      } else {
  //
  //        // Retrieve user information using Google access token
  //        HttpRequest request =
  //            HttpRequest.newBuilder()
  //                .uri(URI.create("https://www.googleapis.com/oauth2/v3/userinfo"))
  //                .header("Authorization", "Bearer " + tokenStr)
  //                .header("Accept", "application/json")
  //                .GET()
  //                .build();
  //
  //        HttpResponse<String> response =
  //            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  //
  //        // Convert response body into map
  //        Map<String, Object> userInfo = objectMapper.readValue(response.body(), Map.class);
  //
  //        email = (String) userInfo.get("email");
  //        name = (String) userInfo.get("name");
  //      }
  //
  //      // Validate email received from Googl
  //      if (email == null) {
  //
  //        throw new GeneralStatusCodeErrorException(
  //            new ErrorMessage(
  //                "Google user email is null",
  //                "Google Login Failed",
  //                HttpStatus.BAD_REQUEST,
  //                HttpStatusCodeEnum.BAD_REQUEST.getValue(),
  //                ErrorMessageCodeEnum.LOGIN_FAILED.getCode(),
  //                new java.util.Date().toString(),
  //                null));
  //      }
  //
  //      // Use email prefix as default name if name is unavailable
  //      if (name == null) {
  //        name = email.split("@")[0];
  //      }
  //
  //      // Retrieve existing user or create a new OAuth user
  //      User user = getOrCreateOAuthUser(correlationId, logKey, email, name);
  //
  //      // Generate JWT token
  //      UserRole role = UserRole.fromValue(user.getRoleId());
  //
  //      String token = jwtUtil.generateToken(user.getEmail(), role.name(), user.getId());
  //
  //      return LoginResponse.builder().token(token).email(user.getEmail()).role(role).build();
  //
  //    } catch (GeneralStatusCodeErrorException e) {
  //
  //      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());
  //
  //      throw e;
  //
  //    } catch (Exception e) {
  //
  //      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());
  //
  //      throw new GeneralStatusCodeErrorException(new
  // ErrorMessage(ApiError.INTERNAL_SERVER_ERROR));
  //    }
  //  }
  //
  //  /**
  //   * Login With GitHub
  //   *
  //   * @param correlationId String
  //   * @param logKey String
  //   * @param code String
  //   * @return LoginResponse
  //   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
  //   */
  //  @Override
  //  public LoginResponse loginWithGithub(String correlationId, String logKey, String code)
  //      throws GeneralStatusCodeErrorException {
  //
  //    try {
  //
  //      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);
  //
  //      // Create request body to exchange authorization code for access token
  //      Map<String, String> tokenRequest =
  //          Map.of(
  //              "client_id", oauthConfig.getGithub().getClientId(),
  //              "client_secret", oauthConfig.getGithub().getClientSecret(),
  //              "code", code);
  //
  //      String requestBody = objectMapper.writeValueAsString(tokenRequest);
  //
  //      // Send request to GitHub OAuth token endpoint
  //      HttpRequest request =
  //          HttpRequest.newBuilder()
  //              .uri(URI.create("https://github.com/login/oauth/access_token"))
  //              .header("Content-Type", "application/json")
  //              .header("Accept", "application/json")
  //              .POST(HttpRequest.BodyPublishers.ofString(requestBody))
  //              .build();
  //
  //      HttpResponse<String> response =
  //          httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  //
  //      // Parse access token response
  //      Map<String, Object> tokenResponse = objectMapper.readValue(response.body(), Map.class);
  //
  //      String accessToken = (String) tokenResponse.get("access_token");
  //
  //      // Validate access token
  //      if (accessToken == null) {
  //
  //        throw new GeneralStatusCodeErrorException(
  //            new ErrorMessage(
  //                "GitHub access token not found",
  //                "GitHub Login Failed",
  //                HttpStatus.UNAUTHORIZED,
  //                HttpStatusCodeEnum.UNAUTHORIZED.getValue(),
  //                ErrorMessageCodeEnum.LOGIN_FAILED.getCode(),
  //                new java.util.Date().toString(),
  //                null));
  //      }
  //
  //      // Retrieve GitHub user profile
  //      HttpRequest userRequest =
  //          HttpRequest.newBuilder()
  //              .uri(URI.create("https://api.github.com/user"))
  //              .header("Authorization", "Bearer " + accessToken)
  //              .header("Accept", "application/json")
  //              .GET()
  //              .build();
  //
  //      // Parse GitHub profile response
  //      HttpResponse<String> userResponse =
  //          httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());
  //
  //      Map<String, Object> githubUser = objectMapper.readValue(userResponse.body(), Map.class);
  //
  //      String email = (String) githubUser.get("email");
  //
  //      String name = (String) githubUser.get("name");
  //
  //      // Use GitHub username if display name is unavailable
  //      if (name == null || name.trim().isEmpty()) {
  //        name = (String) githubUser.get("login");
  //      }
  //
  //      // Validate email address
  //      if (email == null || email.trim().isEmpty()) {
  //
  //        throw new GeneralStatusCodeErrorException(
  //            new ErrorMessage(
  //                "GitHub email not found",
  //                "GitHub Login Failed",
  //                HttpStatus.BAD_REQUEST,
  //                HttpStatusCodeEnum.BAD_REQUEST.getValue(),
  //                ErrorMessageCodeEnum.LOGIN_FAILED.getCode(),
  //                new java.util.Date().toString(),
  //                null));
  //      }
  //
  //      User user = getOrCreateOAuthUser(correlationId, logKey, email, name);
  //
  //      UserRole role = UserRole.fromValue(user.getRoleId());
  //
  //      String token = jwtUtil.generateToken(user.getEmail(), role.name(), user.getId());
  //
  //      return LoginResponse.builder().token(token).email(user.getEmail()).role(role).build();
  //
  //    } catch (GeneralStatusCodeErrorException e) {
  //
  //      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());
  //
  //      throw e;
  //
  //    } catch (Exception e) {
  //
  //      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());
  //
  //      throw new GeneralStatusCodeErrorException(new
  // ErrorMessage(ApiError.INTERNAL_SERVER_ERROR));
  //    }
  //  }
  //
  //  /**
  //   * Get Or Create OAuth User
  //   *
  //   * @param correlationId String
  //   * @param logKey String
  //   * @param email String
  //   * @param name String
  //   * @return User
  //   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
  //   */
  //  private User getOrCreateOAuthUser(String correlationId, String logKey, String email, String
  // name)
  //      throws GeneralStatusCodeErrorException {
  //    try {
  //      return userRepository
  //          .findByEmail(correlationId, logKey, email)
  //          .orElseGet(
  //              () -> {
  //                User newUser =
  //                    User.builder()
  //                        .fullName(name)
  //                        .email(email)
  //                        .password(null)
  //                        .roleId(UserRole.USER.getValue())
  //                        .createdAt(Timestamp.valueOf(LocalDateTime.now()))
  //                        .build();
  //                long id = 0;
  //                try {
  //                  id = userRepository.save(correlationId, logKey, newUser);
  //                  // Set user_id and created_by to the user's own numeric ID after insert
  //                  newUser.setId(id);
  //                  newUser.setUserId(id);
  //                  newUser.setCreatedBy(String.valueOf(id));
  //                  userRepository.update(correlationId, logKey, newUser);
  //                } catch (Exception e) {
  //                  throw new RuntimeException(e);
  //                }
  //                return newUser;
  //              });
  //    } catch (Exception e) {
  //      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());
  //      throw new GeneralStatusCodeErrorException(new
  // ErrorMessage(ApiError.INTERNAL_SERVER_ERROR));
  //    }
  //  }
}
