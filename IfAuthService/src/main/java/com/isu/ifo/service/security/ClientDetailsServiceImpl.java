package com.isu.ifo.service.security;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;

import com.isu.ifo.entity.ClientDetailsImpl;
import com.isu.ifo.repo.ClientRepository;

/**
 * Client 인증시 사용되는 서비스 클래스. 
 * DB에서 ClientDetails객체를 가져온다.
 * Spring Security를 다루어보았다면 UserDetailsService와 동일한 역할을 한다.
 * @author yun-yeoseong
 *
 */
@Primary
@Service
public class ClientDetailsServiceImpl extends JdbcClientDetailsService{
    
	
	@Autowired ClientRepository clientRepo;
	
	
    public ClientDetailsServiceImpl(DataSource dataSource) {
        super(dataSource);
    }
 
    public ClientDetailsImpl findByClientId(String clientId) {
    	return clientRepo.findById(clientId).get();
    }
    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
    	System.out.println("loadClientByClientId :: " + clientId);
        return super.loadClientByClientId(clientId);
    }
 
    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        super.addClientDetails(clientDetails);
    }
 
    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        super.updateClientDetails(clientDetails);
    }
 
    @Override
    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        super.updateClientSecret(clientId, secret);
    }
 
    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        super.removeClientDetails(clientId);
    }
 
    @Override
    public List<ClientDetails> listClientDetails() {
        List<ClientDetails> list = super.listClientDetails();
        return list;
    }
    
}


