package com.todo.todoApp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.todo.todoApp.data.entity.SubTask;
import com.todo.todoApp.data.entity.Todo;
import com.todo.todoApp.data.entity.UserProfile;
import com.todo.todoApp.data.repository.TodoRepository;
import com.todo.todoApp.data.repository.UserRepository;
import com.todo.todoApp.model.dto.TodoDto;
import com.todo.todoApp.model.enumaration.Priority;
import com.todo.todoApp.service.TodoService;
import com.todo.todoApp.service.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private TodoRepository todoRepository;

  @InjectMocks
  private TodoService todoService;

  @Test
  void testAddTodo() {
    // Arrange
    TodoDto todoDto = new TodoDto("testTodo", "testDescription", false, Priority.low, new ArrayList<>());

    Todo todo = Todo.builder()
        .title(todoDto.getTitle())
        .description(todoDto.getDescription())
        .done(todoDto.isDone())
        .priority(todoDto.getPriority())
        .subTasks(new ArrayList<>())
        .build();

    UserProfile user = UserProfile.builder()
        .username("testUser")
        .password("testPassword")
        .build();

    user.setTodos(new ArrayList<>());

    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

    // Act
    ResponseEntity<Todo> response = todoService.addTodo(todoDto, JwtUtils.generateJwtToken("testUser"));

    assertEquals(todo.getTitle(), Objects.requireNonNull(response.getBody()).getTitle());
    assertEquals(todo.getDescription(), response.getBody().getDescription());
    assertEquals(todo.isDone(), response.getBody().isDone());
    assertEquals(todo.getPriority(), response.getBody().getPriority());
    assertEquals(todo.getSubTasks(), response.getBody().getSubTasks());

  }

  @Test
  void testEditTodo() {
    // Mock user profile, existing Todo, and edited Todo
    UserProfile user = new UserProfile();
    Todo existingTodo = new Todo();
    existingTodo.setId(1L);
    existingTodo.setTitle("Existing Todo");
    existingTodo.setSubTasks(new ArrayList<>());
    user.setTodos(new ArrayList<>());
    user.getTodos().add(existingTodo);

    Todo editedTodo = new Todo();
    editedTodo.setTitle("Edited Todo");
    editedTodo.setSubTasks(new ArrayList<>());

    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    when(todoRepository.save(any())).thenReturn(editedTodo);

    // Call the service method
    ResponseEntity<Todo> responseEntity = todoService.editTodo(editedTodo, "1",
        JwtUtils.generateJwtToken("testUser"));

    // Verify that the Todo was edited
    assertEquals("Edited Todo", existingTodo.getTitle());

    // Verify the response
    assertNotNull(responseEntity.getBody());
    assertEquals("Edited Todo", responseEntity.getBody().getTitle());
    assertEquals(HttpStatusCode.valueOf(200), responseEntity.getStatusCode());
  }

  @Test
  void testGetTodos() {
    // Mock user profile
    UserProfile user = new UserProfile();
    user.setTodos(new ArrayList<>());
    Todo todo1 = new Todo();
    todo1.setTitle("Todo 1");
    todo1.setSubTasks(new ArrayList<>());
    Todo todo2 = new Todo();
    todo2.setTitle("Todo 2");
    todo2.setSubTasks(new ArrayList<>());
    user.addTodo(todo1);
    user.addTodo(todo2);

    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

    // Call the service method
    ResponseEntity<List<Todo>> responseEntity = todoService.getTodos(JwtUtils.generateJwtToken("testUser"));

    // Verify the response
    assertNotNull(responseEntity.getBody());
    assertEquals(2, responseEntity.getBody().size());
    assertEquals("Todo 2", responseEntity.getBody().get(0).getTitle());
    assertEquals("Todo 1", responseEntity.getBody().get(1).getTitle());
    assertEquals(HttpStatusCode.valueOf(200), responseEntity.getStatusCode());
  }

  @Test
  void testGetTodoById() {
    // Mock user profile and an existing Todo
    UserProfile user = new UserProfile();
    user.setTodos(new ArrayList<>());
    Todo existingTodo = new Todo();
    existingTodo.setId(1L);
    existingTodo.setTitle("Existing Todo");
    existingTodo.setSubTasks(new ArrayList<>());
    user.getTodos().add(existingTodo);

    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

    // Call the service method
    ResponseEntity<Todo> responseEntity = todoService.getTodoById(JwtUtils.generateJwtToken("testUser"),
        "1");

    // Verify the response
    assertNotNull(responseEntity.getBody());
    assertEquals("Existing Todo", responseEntity.getBody().getTitle());
    assertEquals(HttpStatusCode.valueOf(200), responseEntity.getStatusCode());
  }

  @Test
  void testDeleteTodoById() {
    // Mock user profile and an existing Todo
    UserProfile user = new UserProfile();
    user.setTodos(new ArrayList<>());
    Todo existingTodo = new Todo();
    existingTodo.setId(1L);
    existingTodo.setTitle("Existing Todo");
    existingTodo.setSubTasks(new ArrayList<>());
    user.getTodos().add(existingTodo);

    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

    // Call the service method
    ResponseEntity<Todo> responseEntity = todoService.deleteTodoById(JwtUtils.generateJwtToken("testUser"), "1");

    // Verify that the Todo was deleted
    assertTrue(user.getTodos().isEmpty());

    // Verify the response
    assertNotNull(responseEntity.getBody());
    assertEquals("Existing Todo", responseEntity.getBody().getTitle());
    assertEquals(HttpStatusCode.valueOf(200), responseEntity.getStatusCode());
  }

  @Test
  void testDeleteSubTaskByID() {
    // Mock user profile, existing Todo, and a subtask
    UserProfile user = new UserProfile();
    user.setTodos(new ArrayList<>());
    Todo existingTodo = new Todo();
    existingTodo.setId(1L);
    existingTodo.setTitle("Existing Todo");
    existingTodo.setSubTasks(new ArrayList<>());

    SubTask subTask = new SubTask();
    subTask.setId(1L);
    subTask.setTitle("SubTask 1");
    existingTodo.getSubTasks().add(subTask);

    user.getTodos().add(existingTodo);

    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

    // Call the service method
    ResponseEntity<SubTask> responseEntity = todoService.deleteSubTaskByID(JwtUtils.generateJwtToken("testUser"), "1", "1");

    // Verify that the SubTask was deleted
    assertTrue(existingTodo.getSubTasks().isEmpty());

    // Verify the response
    assertNotNull(responseEntity.getBody());
    assertEquals("SubTask 1", responseEntity.getBody().getTitle());
    assertEquals(HttpStatusCode.valueOf(200), responseEntity.getStatusCode());
  }


}
