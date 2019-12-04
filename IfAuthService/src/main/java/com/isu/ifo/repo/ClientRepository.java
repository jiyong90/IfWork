package com.isu.ifo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifo.entity.ClientDetailsImpl;


@Repository
public interface ClientRepository extends JpaRepository<ClientDetailsImpl, String>{
    
}


