package com.isu.ifo.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestTemplateConfig {
	 	@Autowired
	    CloseableHttpClient httpClient;
	 
	    @Bean
	    public RestTemplate restTemplate() {
	        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
	        return restTemplate;
	    }
	 
	    @Bean
	    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
	    		int timeout = 5000;
	        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
	        clientHttpRequestFactory.setHttpClient(httpClient);
	        clientHttpRequestFactory.setConnectTimeout(timeout);
	        clientHttpRequestFactory.setConnectionRequestTimeout(timeout);
	        clientHttpRequestFactory.setReadTimeout(timeout);
	        return clientHttpRequestFactory;
	    }
	  
	    /*
	    private ClientHttpRequestFactory getClientHttpRequestFactory() {
	        int timeout = 5000;
	        RequestConfig config = RequestConfig.custom()
	          .setConnectTimeout(timeout)
	          .setConnectionRequestTimeout(timeout)
	          .setSocketTimeout(timeout)
	          .build();
	        CloseableHttpClient client = HttpClientBuilder
	          .create()
	          .setDefaultRequestConfig(config)
	          .build();
	        return new HttpComponentsClientHttpRequestFactory(client);
	    }
	    */
	    
}
