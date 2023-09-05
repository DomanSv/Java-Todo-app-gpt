package com.todo.todoApp.model.dto;

import com.todo.todoApp.model.enumaration.Priority;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class TodoDto {

  @NotNull
  @Size(min = 2, max = 50)
  private String title;

  @Size(max = 500)
  private String description;
  private boolean done;
  @Enumerated(EnumType.STRING)
  private Priority priority;
  @Size(max = 5)
  private List<SubTaskDto> subTasks;


}
