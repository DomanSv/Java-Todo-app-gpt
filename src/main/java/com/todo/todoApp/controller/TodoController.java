package com.todo.todoApp.controller;

import com.todo.todoApp.data.entity.SubTask;
import com.todo.todoApp.data.entity.Todo;
import com.todo.todoApp.model.dto.TodoDto;
import com.todo.todoApp.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/todos")
@RequiredArgsConstructor
@Validated
public class TodoController {

  private final TodoService todoService;

  @PostMapping
  public ResponseEntity<Todo> addTodo(@RequestHeader("Authorization") String token, @RequestBody @Valid final TodoDto todoDto) {
    return todoService.addTodo(todoDto, token);
  }

  @PostMapping("/{todoId}")
  public ResponseEntity<Todo> editTodo(@RequestHeader("Authorization") String token, @RequestBody @Valid final Todo editedTodo,
      @PathVariable String todoId) {
    return todoService.editTodo(editedTodo, todoId, token);
  }

  @GetMapping
  public ResponseEntity<List<Todo>> getTodos(@RequestHeader("Authorization") String token) {
    return todoService.getTodos(token);
  }

  @GetMapping("/{todoId}")
  public ResponseEntity<Todo> getTodoById(@RequestHeader("Authorization") String token , @PathVariable String todoId) {
    return todoService.getTodoById(token, todoId);
  }

  @DeleteMapping("/{todoId}")
  public ResponseEntity<Todo> deleteTodoById(@RequestHeader("Authorization") String token , @PathVariable String todoId){
    return todoService.deleteTodoById(token, todoId);
  }

  @DeleteMapping("/{todoId}/{subTaskId}")
  public ResponseEntity<SubTask> deleteSubTaskByID(@RequestHeader("Authorization") String token ,
      @PathVariable String todoId, @PathVariable String subTaskId){
    return todoService.deleteSubTaskByID(token, todoId, subTaskId);
  }


}
