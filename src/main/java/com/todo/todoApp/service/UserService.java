package com.todo.todoApp.service;

import static com.todo.todoApp.constants.ApiConstants.NO_USER_WITH_THESE_CREDENTIALS_FOUND;
import static com.todo.todoApp.constants.ApiConstants.PASSWORD_DOES_NOT_MATCH;
import static java.util.Objects.isNull;

import com.todo.todoApp.data.entity.UserProfile;
import com.todo.todoApp.data.repository.UserRepository;
import com.todo.todoApp.model.dto.UserDto;
import com.todo.todoApp.service.utils.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  @Transactional
  public ResponseEntity<String> registerUser(UserDto userDto) {

    if (userRepository.existsByUsername(userDto.getUsername())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Потребител с това име вече съществува!");
    }
    String hashedPassword = BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt());

      UserProfile user = UserProfile.builder()
          .username(userDto.getUsername())
          .password(hashedPassword)
          .build();

      userRepository.save(user);

      return ResponseEntity.ok("Потребителят се регистрира успешно!");
  }

  @Transactional
  public ResponseEntity<String> loginUser(UserDto userDto) {

    final UserProfile user = userRepository.findByUsername(userDto.getUsername()).orElse(null);

    if (isNull(user)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(NO_USER_WITH_THESE_CREDENTIALS_FOUND);
    }

    if (!BCrypt.checkpw(userDto.getPassword(), user.getPassword())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(PASSWORD_DOES_NOT_MATCH);
    }

    String token = JwtUtils.generateJwtToken(userDto.getUsername());

    return ResponseEntity.ok(token);
  }

  @Transactional
  public ResponseEntity<UserProfile> getAccount(String token) {

    String formattedToken = token.replace("Bearer ", "").replace("\"","");

    String username = JwtUtils.extractUsernameFromToken(formattedToken);
    UserProfile user = userRepository.findByUsername(username).orElse(null);
    if (isNull(user)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
    return ResponseEntity.ok(user);
  }


}
