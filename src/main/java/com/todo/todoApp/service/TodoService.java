package com.todo.todoApp.service;

import static java.util.Objects.isNull;

import com.todo.todoApp.data.entity.SubTask;
import com.todo.todoApp.data.entity.Todo;
import com.todo.todoApp.data.entity.UserProfile;
import com.todo.todoApp.data.repository.TodoRepository;
import com.todo.todoApp.data.repository.UserRepository;
import com.todo.todoApp.model.dto.SubTaskDto;
import com.todo.todoApp.model.dto.TodoDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.todo.todoApp.service.utils.JwtUtils;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

  public static final String BEARER = "Bearer ";
  private final UserRepository userRepository;
  private final TodoRepository todoRepository;

  @Transactional
  public ResponseEntity<Todo> addTodo(TodoDto todoDto, String token) {

    UserProfile user = getUserFromToken(token);

    if (isNull(user)) {
      return ResponseEntity.badRequest().build();
    }

    List<SubTask> subTasks = new ArrayList<>();

    for (SubTaskDto subTask : todoDto.getSubTasks()) {
      subTasks.add(SubTask.builder().title(subTask.getTitle()).build());
    }

    Todo todo = Todo.builder()
        .title(todoDto.getTitle())
        .description(todoDto.getDescription())
        .done(todoDto.isDone())
        .priority(todoDto.getPriority())
        .subTasks(subTasks)
        .build();

    user.getTodos().add(todo);

    return ResponseEntity.ok(todo);
  }

  public ResponseEntity<Todo> editTodo(Todo editedTodo, String todoId, String token) {

    UserProfile user = getUserFromToken(token);

    if (isNull(user)) {
      return ResponseEntity.badRequest().build();
    }

    Todo todo = user.getTodos().stream().filter(t -> t.getId().equals(Long.parseLong(todoId))).findFirst().orElse(null);

    if (isNull(todo)) {
      return ResponseEntity.badRequest().build();
    }

    todo.setTitle(editedTodo.getTitle());
    todo.setDescription(editedTodo.getDescription());
    todo.setDone(editedTodo.isDone());
    todo.setPriority(editedTodo.getPriority());
    todo.getSubTasks().clear();
    todo.getSubTasks().addAll(editedTodo.getSubTasks());

    todoRepository.save(todo);

    return ResponseEntity.ok(todo);

  }

  public ResponseEntity<List<Todo>> getTodos(String token) {

    UserProfile user = getUserFromToken(token);

    if (isNull(user)) {
      return ResponseEntity.badRequest().build();
    }

    reverseList(user.getTodos());

    return ResponseEntity.ok(user.getTodos());
  }

  private void reverseList(List<Todo> todos) {
    List<Todo> reversedList = new ArrayList<>();
    for (int i = todos.size() - 1; i >= 0; i--) {
      reversedList.add(todos.get(i));
    }
    todos.clear();
    todos.addAll(reversedList);
  }

  public ResponseEntity<Todo> getTodoById(String token, String todoId) {

    UserProfile user = getUserFromToken(token);

    if (isNull(user)) {
      return ResponseEntity.badRequest().build();
    }

    Todo todo = user.getTodos().stream().filter(t -> t.getId().equals(Long.parseLong(todoId))).findFirst().orElse(null);

    if (isNull(todo)) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok(todo);
  }

  public ResponseEntity<Todo> deleteTodoById(String token, String todoId) {

    UserProfile user = getUserFromToken(token);

    if (isNull(user)) {
      return ResponseEntity.badRequest().build();
    }

    Todo todo = user.getTodos().stream().filter(t -> t.getId().equals(Long.parseLong(todoId))).findFirst().orElse(null);

    if (isNull(todo)) {
      return ResponseEntity.badRequest().build();
    }

    user.getTodos().remove(todo);

    userRepository.save(user);

    return ResponseEntity.ok(todo);

  }

  public ResponseEntity<SubTask> deleteSubTaskByID(String token, String todoId, String subTaskId) {

    UserProfile user = getUserFromToken(token);

    if (isNull(user)) {
      return ResponseEntity.badRequest().build();
    }

    Todo todo = user.getTodos().stream().filter(t -> t.getId().equals(Long.parseLong(todoId))).findFirst().orElse(null);

    if (isNull(todo)) {
      return ResponseEntity.badRequest().build();
    }

    SubTask subTask = todo.getSubTasks().stream().filter(s -> s.getId().equals(Long.parseLong(subTaskId)))
        .findFirst().orElse(null);

    if (isNull(subTask)) {
      return ResponseEntity.badRequest().build();
    }

    todo.removeSubTask(subTask);
    todoRepository.save(todo);

    return ResponseEntity.ok(subTask);

  }

  private UserProfile getUserFromToken(String token) {

    String formattedToken = token.replace(BEARER, "").replace("\"","");

    String username = JwtUtils.extractUsernameFromToken(formattedToken);

    return userRepository.findByUsername(username).orElse(null);

  }

}
