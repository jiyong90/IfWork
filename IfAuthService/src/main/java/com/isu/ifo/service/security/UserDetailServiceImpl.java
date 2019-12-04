package com.isu.ifo.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.isu.ifo.entity.User;
import com.isu.ifo.entity.UserDetailsImpl;
import com.isu.ifo.repo.UserJpaRepo;
import com.isu.ifo.repo.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserDetailServiceImpl implements UserDetailsService {

	@Autowired private UserRepository repository;
	
    private AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public UserDetails loadUserByUsername(String name) {
    	
    	System.out.println("CustomUserDetailService loadUserByUsername name : " + name);
    	
        UserDetailsImpl user = repository.findByUsername(name);
        
        if(user == null)
        	throw new UsernameNotFoundException("user is not exists");
        
        detailsChecker.check(user);
        return user;
    }
    

    public UserDetailsImpl save(UserDetailsImpl user) {
        return repository.save(user);
    }
    
}
