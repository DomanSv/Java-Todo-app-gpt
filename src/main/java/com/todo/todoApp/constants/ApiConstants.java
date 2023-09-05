package com.todo.todoApp.constants;

public final class ApiConstants {

  public static final String INVALID_PASSWORD_REGEX = "Password must contain at least 8 characters, one uppercase letter, one lowercase letter and one number!";
  public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

  public static final String NO_USER_WITH_THESE_CREDENTIALS_FOUND = "Не е намерен потребител с това име!";
  public static final String PASSWORD_DOES_NOT_MATCH = "Неправилна парола!";

  private ApiConstants() {
  }

}
