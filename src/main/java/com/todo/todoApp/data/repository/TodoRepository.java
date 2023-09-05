package com.todo.todoApp.data.repository;

import com.todo.todoApp.data.entity.Todo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long>, JpaSpecificationExecutor<Todo> {

  @Override
  Optional<Todo> findById(Long aLong);

}
