package com.todo.todoApp.data.entity;

import com.todo.todoApp.model.enumaration.Priority;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "todos", schema = "todoapp")
public class Todo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(insertable = false, updatable = false, name = "id")
  private Long id;

  private String title;

  private String description;

  private boolean done;

  @Enumerated(EnumType.STRING)
  private Priority priority;

  @Column(name = "ai_response")
  @Size(max = 1000)
  private String aIResponse;

  @Column(name = "has_ai_response")
  private boolean hasAIResponse;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "todo_id")
  private List<SubTask> subTasks;

  public void addSubTask(SubTask subTask) {
    subTasks.add(subTask);
  }

  public void removeSubTask(SubTask subTask) {
    subTasks.remove(subTask);
  }

  @Override
  public String toString() {
    return "Todo{" +
        ", title='" + title + '\'' +
        ", description='" + description + '\'' +
        ", subTasks=" + subTasks +
        '}';
  }


}
