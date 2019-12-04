package com.isu.ifo.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	/*
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
    private UserJpaRepo userJpaRepo;
	 */
    @Override
    public Authentication authenticate(Authentication authentication) {
    	System.out.println(" CustomAuthenticationProvider :::::::::: ");
    	ObjectMapper mapper = new ObjectMapper();
    	try {
			System.out.println(mapper.writeValueAsString(authentication.getDetails()));
			System.out.println(mapper.writeValueAsString(authentication.getAuthorities()));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials() , authorities);
    	
        authRequest.setDetails(authentication.getDetails());
        
        Authentication auth = authRequest;
    	
    	return auth;
    	/*
    	System.out.println("============================== authenticate ==============================");
        String name = authentication.getName();
     // 첫번째 방법 
        System.out.println(" auth : " + authentication.toString()); 

        
        String password = authentication.getCredentials().toString();

    	System.out.println("============================== name : " + name);
    	System.out.println("============================== password : " + password);
    	
        User user = userJpaRepo.findByUsername(name).orElseThrow(() -> new UsernameNotFoundException("user is not exists"));

    	System.out.println("============================== user.getPassword() : " + user.getPassword());
    	System.out.println("============================== user password() encode : " + passwordEncoder.encode(password));

    	System.out.println("============================== user password() encode : " + passwordEncoder.encode(password));
    	//if(!passwordEncoder.encode(password).equals(user.getPassword())) {
        
    	//if (!passwordEncoder.matches(password, password)){
        //    throw new BadCredentialsException("password is not valid");
    	//}
    	
    	System.out.println("user.getAuthorities : " + user.getAuthorities());
    	Authentication a = new UsernamePasswordAuthenticationToken(name, password, user.getAuthorities());
        System.out.println(" a : " + a.toString()); 
        return a;
        */
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
                UsernamePasswordAuthenticationToken.class);
    }
}
