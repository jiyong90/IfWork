package com.isu.ifo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isu.ifo.entity.UserDetailsImpl;

public interface UserRepository extends JpaRepository<UserDetailsImpl, Long>{
    
    public UserDetailsImpl findByUsername(String username);
}


