package com.isu.ifo.service;

import com.isu.ifo.entity.UserDetailsImpl;

public interface UserService {
    
    public UserDetailsImpl findByUsername(String username);
    public UserDetailsImpl save(UserDetailsImpl user);
}
 


