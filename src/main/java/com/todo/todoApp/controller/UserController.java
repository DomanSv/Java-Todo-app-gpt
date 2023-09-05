package com.todo.todoApp.controller;

import com.todo.todoApp.data.entity.UserProfile;
import com.todo.todoApp.model.dto.UserDto;
import com.todo.todoApp.service.UserService;
import com.todo.todoApp.service.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
@Validated
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@RequestBody @Valid final UserDto userDto) {
    return userService.registerUser(userDto);
  }

  @PostMapping("/login")
  public ResponseEntity<String> loginUser(@RequestBody @Valid final UserDto userDto) {
    return userService.loginUser(userDto);
  }

  @GetMapping("/account")
  public ResponseEntity<UserProfile> getAccount(@RequestHeader("Authorization") String token) {
    return userService.getAccount(token);
  }

}
