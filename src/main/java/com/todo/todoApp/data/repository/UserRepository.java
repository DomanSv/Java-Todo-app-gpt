package com.todo.todoApp.data.repository;

import com.todo.todoApp.data.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<UserProfile, Long>, JpaSpecificationExecutor<UserProfile> {

  @Override
  Optional<UserProfile> findById(Long aLong);
  Optional<UserProfile> findByUsername(String username);
  boolean existsByUsername(String username);
}
