package com.todo.todoApp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.todo.todoApp.data.entity.UserProfile;
import com.todo.todoApp.data.repository.UserRepository;
import com.todo.todoApp.model.dto.UserDto;
import com.todo.todoApp.service.UserService;
import com.todo.todoApp.service.utils.JwtUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Test
  void testRegisterUser() {
    // Arrange
    UserDto userDto = new UserDto("testuser", "Test1234");

    when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);

    // Act
    ResponseEntity<String> response = userService.registerUser(userDto);

    // Assert
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals("Потребителят се регистрира успешно!", response.getBody());
    verify(userRepository, times(1)).save(any(UserProfile.class));

  }
  @Test
  void testRegisterUserConflict() {
    // Arrange
    UserDto userDto = new UserDto("testuser", "Test1234");

    when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(true);

    // Act
    ResponseEntity<String> response = userService.registerUser(userDto);

    // Assert
    Assertions.assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    Assertions.assertEquals("Потребител с това име вече съществува!", response.getBody());
    verify(userRepository, never()).save(any(UserProfile.class));
  }

  @Test
  void testLoginUserSuccess() {
    // Arrange
    UserDto userDto = new UserDto("testuser", "Test1234");
    UserProfile user = UserProfile.builder()
        .username("testuser")
        .password(BCrypt.hashpw("Test1234", BCrypt.gensalt()))
        .build();

    when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(user));

    // Act
    ResponseEntity<String> response = userService.loginUser(userDto);

    // Assert
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void testLoginUserUnauthorized() {
    // Arrange
    UserDto userDto = new UserDto("testuser", "Test1234");

    when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());

    // Act
    ResponseEntity<String> response = userService.loginUser(userDto);

    // Assert
    Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    Assertions.assertEquals("Не е намерен потребител с това име!", response.getBody());
  }

  @Test
  void testLoginUserInvalidPassword() {
    // Arrange
    UserDto userDto = new UserDto("testuser", "Test1234");
    UserProfile user = UserProfile.builder()
        .username("testuser")
        .password(BCrypt.hashpw("WrongPassword", BCrypt.gensalt()))
        .build();

    when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(user));

    // Act
    ResponseEntity<String> response = userService.loginUser(userDto);

    // Assert
    Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    Assertions.assertEquals("Неправилна парола!", response.getBody());
  }

  @Test
  void testGetAccount() {

    UserDto userDto = new UserDto("testuser", "Test1234");
    UserProfile user = UserProfile.builder()
        .username("testuser")
        .password(BCrypt.hashpw("password", BCrypt.gensalt()))
        .build();

    when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(user));
    String token = JwtUtils.generateJwtToken(userDto.getUsername());

    // Act
    ResponseEntity<UserProfile> response = userService.getAccount(token);

    // Assert
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());

  }

  @Test
  void testGetAccountInvalidToken() {

    String token = JwtUtils.generateJwtToken("INVALID");

    // Act
    ResponseEntity<UserProfile> response = userService.getAccount(token);

    // Assert
    Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    Assertions.assertNull(response.getBody());

  }



}
