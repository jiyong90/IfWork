package com.isu.ifo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifo.entity.UserDetailsImpl;
import com.isu.ifo.repo.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired private UserRepository repository;
    
    @Override
    public UserDetailsImpl findByUsername(String username) {
        return repository.findByUsername(username);
    }
 
    @Override
    public UserDetailsImpl save(UserDetailsImpl user) {
        return repository.save(user);
    }
    
    
}


