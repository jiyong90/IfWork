package com.isu.ifa;

public class GatewayConfiguration {}
//@Configuration
//@EnableResourceServer
//public class GatewayConfiguration extends WebSecurityConfigurerAdapter {
//	
//	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
//			"classpath:/META-INF/resources/", "classpath:/resources/",
//			"classpath:/static/", "classpath:/public/" };
//	
//    @Override
//    public void configure(final HttpSecurity http) throws Exception {
//    http.authorizeRequests()
//          .antMatchers("/if*/**", "/static/**", "/*-api/**")
//          .permitAll()
//          .antMatchers("/**")
//          .authenticated().and().csrf().disable();
//    }
//    
//    
//    /*
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//    	if (!registry.hasMappingForPattern("/webjars/**")) {
//    		registry.addResourceHandler("/webjars/**").addResourceLocations(
//    				"classpath:/META-INF/resources/webjars/");
//    	}
//    	if (!registry.hasMappingForPattern("/**")) {
//    		registry.addResourceHandler("/**").addResourceLocations(
//    				RESOURCE_LOCATIONS);
//    	}
//    }
//    */
//    
//}

