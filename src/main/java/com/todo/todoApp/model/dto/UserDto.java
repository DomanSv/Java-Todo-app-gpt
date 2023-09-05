package com.todo.todoApp.model.dto;

import static com.todo.todoApp.constants.ApiConstants.INVALID_PASSWORD_REGEX;
import static com.todo.todoApp.constants.ApiConstants.PASSWORD_REGEX;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

  @NotNull
  @Size(min = 2, max = 50)
  private String username;

  @NotNull
  @Pattern(regexp = PASSWORD_REGEX, message = INVALID_PASSWORD_REGEX)
  private String password;

}
