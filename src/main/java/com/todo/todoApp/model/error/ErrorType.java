package com.todo.todoApp.model.error;


import com.todo.todoApp.model.enumaration.ErrorCode;

public record ErrorType(ErrorCode code, String message) {
}
