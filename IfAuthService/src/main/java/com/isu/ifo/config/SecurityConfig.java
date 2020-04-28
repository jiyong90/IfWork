package com.isu.ifo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.isu.ifo.handler.CustomAuthenticationEntryPoint;
import com.isu.ifo.handler.CustomAuthenticationFailureHandler;
import com.isu.ifo.handler.CustomAuthenticationProvider;
import com.isu.ifo.handler.CustomAuthenticationSuccessHandler;
import com.isu.ifo.handler.CustomLogoutSuccessHandler;
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
    	 //((HttpSecurity)((HttpSecurity)((HttpSecurity)((HttpSecurity)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((HttpSecurity)((HttpSecurity)((HttpSecurity)((HttpSecurity)
    			 security
    		      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
    		      .and()
    		      .cors().and()
    		      .csrf().disable()
    		      .headers().frameOptions().disable()
    		      .and()
    		      .authorizeRequests().antMatchers(new String[] { "/client/**", "/oauth/**", "/oauth/token", "/oauth2/**", "/h2-console/*" }).permitAll()
    		      .and()
    		      .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
    		      .and()
    		      .addFilter(authenticationFilter())
    		      .formLogin().and()
    		      .httpBasic()
    		      .and().logout()
    		      .logoutRequestMatcher(new AntPathRequestMatcher("/logout/*"))
    		      .clearAuthentication(true).deleteCookies(new String[] { "*" }).invalidateHttpSession(true)
    		      .logoutSuccessHandler(customLogoutSuccessHandler())
    		      ;
    }
    @Bean
    public CustomLogoutSuccessHandler customLogoutSuccessHandler() {
      return new CustomLogoutSuccessHandler();
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
    public CustomAuthenticationSuccessHandler authenticationSuccessHandler() {
        CustomAuthenticationSuccessHandler successHandler = new CustomAuthenticationSuccessHandler();
        //successHandler.setDefaultTargetUrl("/index");
        successHandler.setUseReferer(false);
        successHandler.setTargetUrlParameter("lru");
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
    
    
    @Bean
    public ErrorPageFilter errorPageFilter() {
    	return new ErrorPageFilter();
    }
    
    @Bean
    public FilterRegistrationBean DisabledErrorPageFilter(ErrorPageFilter filter) { 
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean<>(); 
        filterRegistration.setFilter(filter); 
        filterRegistration.setName("disabledErrorPageFilter"); 
        filterRegistration.setEnabled(false); 
        return filterRegistration; 
    }

    
    
}