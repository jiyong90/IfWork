package com.isu.ifo.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.isu.ifo.dto.ClientDetailsImpl;
import com.isu.ifo.dto.RegisterClientInfo;
import com.isu.ifo.entity.ClientType;

/*
* 동적 클라이언트 등록에 사용되는 컨트롤러
*/
@Controller
@RequestMapping("/client")
public class ClientController {
    
    @Autowired private ClientRegistrationService clientRegistrationService;
    
    @Autowired private PasswordEncoder passwordEncoder; 
    
    @GetMapping("/register")
    public ModelAndView registerPage(ModelAndView mav) {
        mav.setViewName("client/register");
        mav.addObject("registry", new RegisterClientInfo());
        return mav;
    }
    
    @GetMapping("/dashboard")
    public ModelAndView dashboard(ModelAndView mv) {
        mv.addObject("applications",
                clientRegistrationService.listClientDetails());
        return mv;
    }
    
    @PostMapping("/save")
    //public ModelAndView 
    public void save(@Valid RegisterClientInfo clientDetails) { //,ModelAndView mav ,BindingResult bindingResult) {
        /*
        if(bindingResult.hasErrors()) {
            return new ModelAndView("client/register");
        }
        */
    	
    	System.out.println("clientDetails.getClientId() : " + clientDetails.getClientId());
        ClientDetailsImpl client = new ClientDetailsImpl();
        client.addAdditionalInformation("name", clientDetails.getName());
        client.setRegisteredRedirectUri(new HashSet<>(Arrays.asList("http://localhost:9000/callback")));
        client.setClientType(ClientType.PUBLIC);
        client.setClientId(clientDetails.getClientId());
        client.setClientSecret(passwordEncoder.encode(clientDetails.getSecret()));
        client.setAccessTokenValiditySeconds(3600);
        client.setScope(Arrays.asList("read","write"));
        clientRegistrationService.addClientDetails(client);
        
        //mav.setViewName("redirect:/client/dashboard");
        
        //return mav;
    }
    
    @GetMapping("/remove")
    public ModelAndView remove(
            @RequestParam(value = "client_id", required = false) String clientId) {
 
        clientRegistrationService.removeClientDetails(clientId);
 
        ModelAndView mv = new ModelAndView("redirect:/client/dashboard");
        mv.addObject("applications",
                clientRegistrationService.listClientDetails());
        return mv;
    }
    
    @GetMapping("/password")
    public String passwordEncoder(
            @RequestParam(value = "secret", required = true) String secret) {
    		System.out.println("passwordEncoder.encode(secret) : " +passwordEncoder.encode(secret));
        	return passwordEncoder.encode(secret);
    }
}
 


