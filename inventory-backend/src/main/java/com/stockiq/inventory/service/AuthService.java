package com.stockiq.inventory.service;

import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.user.CreateUserResponse;
import com.stockiq.inventory.common.response.user.LoginResponse;
import com.stockiq.inventory.common.response.user.ProfileUpdateResponse;
import com.stockiq.inventory.dto.LoginDto;
import com.stockiq.inventory.dto.UserDto;
import com.stockiq.inventory.dto.UpdateProfileDto;
import com.stockiq.inventory.dto.ResetPasswordDto;

public interface AuthService {

  /**
   * Register User
   *
   * @param correlationId String
   * @param logKey String
   * @param userDto UserDto
   * @return CreateUserResponse
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  CreateUserResponse register(String correlationId, String logKey, UserDto userDto)
      throws GeneralStatusCodeErrorException;

  /**
   * Login User
   *
   * @param correlationId String
   * @param logKey String
   * @param loginDto LoginDto
   * @return LoginResponse
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  LoginResponse login(String correlationId, String logKey, LoginDto loginDto)
      throws GeneralStatusCodeErrorException;

  /**
   * Get Current User
   *
   * @param correlationId String
   * @param logKey String
   * @param email String
   * @return CreateUserResponse
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  CreateUserResponse getCurrentUser(String correlationId, String logKey, String email)
      throws GeneralStatusCodeErrorException;

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
  ProfileUpdateResponse updateProfile(
      String correlationId, String logKey, String currentUserEmail, UpdateProfileDto updateDto)
      throws GeneralStatusCodeErrorException;

  /**
   * Reset Password
   *
   * @param correlationId String
   * @param logKey String
   * @param resetDto ResetPasswordDto
   * @return boolean
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  boolean resetPassword(String correlationId, String logKey, ResetPasswordDto resetDto)
      throws GeneralStatusCodeErrorException;

  /**
   * Login With Google
   *
   * @param correlationId String
   * @param logKey String
   * @param idToken String
   * @return LoginResponse
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  LoginResponse loginWithGoogle(String correlationId, String logKey, String idToken)
      throws GeneralStatusCodeErrorException;

  /**
   * Login With GitHub
   *
   * @param correlationId String
   * @param logKey String
   * @param code String
   * @return LoginResponse
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  LoginResponse loginWithGithub(String correlationId, String logKey, String code)
      throws GeneralStatusCodeErrorException;
}
