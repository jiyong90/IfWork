package com.isu.ifo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isu.ifo.entity.User;

public interface UserJpaRepo extends JpaRepository<User, Long> {
    Optional<User> findByUid(String email);
    
    Optional<User> findByUsername(String username);
}
