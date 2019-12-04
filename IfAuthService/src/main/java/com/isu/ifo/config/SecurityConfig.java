package com.isu.ifo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.isu.ifo.handler.CustomAuthenticationEntryPoint;
import com.isu.ifo.handler.CustomAuthenticationFailureHandler;
import com.isu.ifo.handler.CustomAuthenticationProvider;
import com.isu.ifo.handler.CustomAuthenticationSuccessHandler;
import com.isu.ifo.handler.CustomPasswordEncoderFactories;
import com.isu.ifo.handler.CustomUsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired private CustomAuthenticationProvider authenticationProvider;

    @Autowired private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    

    @Bean
    public PasswordEncoder passwordEncoder() {
        return CustomPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
    
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        //return new CustomAuthenticationEntryPoint("/loginPage");
    	return customAuthenticationEntryPoint;
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    } 

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        security.cors().and()
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests().antMatchers("/client/**", "/oauth/**", "/oauth/token", "/oauth2/**", "/h2-console/*").permitAll()
                .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
                .and()
                .addFilter(authenticationFilter())
                .formLogin().and()
                .httpBasic();
    }
    /*
     * Form Login시 걸리는 Filter bean register
     */
    @Bean
    public CustomUsernamePasswordAuthenticationFilter authenticationFilter() throws Exception {
        CustomUsernamePasswordAuthenticationFilter authenticationFilter = new CustomUsernamePasswordAuthenticationFilter(authenticationManager());
        authenticationFilter.setFilterProcessesUrl("/login/**/authorize");
        authenticationFilter.setUsernameParameter("username");
        authenticationFilter.setPasswordParameter("password");
        
        authenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        authenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        
        
        authenticationFilter.afterPropertiesSet();
        
        return authenticationFilter;
    }
    
    /*
     * SuccessHandler bean register
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        CustomAuthenticationSuccessHandler successHandler = new CustomAuthenticationSuccessHandler();
        //successHandler.setDefaultTargetUrl("/index");
        return successHandler;
    }
    
    /*
     * FailureHandler bean register
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        CustomAuthenticationFailureHandler failureHandler = new CustomAuthenticationFailureHandler();
        //failureHandler.setDefaultFailureUrl("/loginPage?error=error");
        return failureHandler;
    }
    
}