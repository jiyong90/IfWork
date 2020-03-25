package com.isu.ifa;
 
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import com.github.mthizo247.cloud.netflix.zuul.web.socket.EnableZuulWebSocket;
import com.isu.ifa.filter.ErrorFilter;
import com.isu.ifa.filter.PostFilter;
import com.isu.ifa.filter.PreFilter;
import com.isu.ifa.filter.RouteFilter;

@EnableZuulProxy
@EnableZuulWebSocket
//@EnableDiscoveryClient
//@EnableFeignClients
@SpringBootApplication
//@EnableCircuitBreaker
@EnableWebSocketMessageBroker
public class ApiGwApplication extends SpringBootServletInitializer{

	
	public static void main(String[] args) {
		SpringApplication.run(ApiGwApplication.class, args);
	}
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// TODO Auto-generated method stub
		return builder.sources(ApiGwApplication.class);
	}
	 
	
	@Bean
	public PreFilter preFilter() {
		return new PreFilter();
	}
	 
	@Bean
	public PostFilter postFilter() {
		return new PostFilter();
	}
	
	@Bean
	public RouteFilter routeFilter() {
		return new RouteFilter();
	}
	
	@Bean
	public ErrorFilter errorFilter() {
		return new ErrorFilter();
	}
	/*
	@Bean
    UserInfoRestTemplateCustomizer userInfoRestTemplateCustomizer(LoadBalancerInterceptor loadBalancerInterceptor) {
        return template -> {
            List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
            interceptors.add(loadBalancerInterceptor);
            AccessTokenProviderChain accessTokenProviderChain = Stream
                    .of(new AuthorizationCodeAccessTokenProvider(), new ImplicitAccessTokenProvider(),
                            new ResourceOwnerPasswordAccessTokenProvider(), new ClientCredentialsAccessTokenProvider())
                    .peek(tp -> tp.setInterceptors(interceptors))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), AccessTokenProviderChain::new));
            template.setAccessTokenProvider(accessTokenProviderChain);
        };
    }
    */
}
