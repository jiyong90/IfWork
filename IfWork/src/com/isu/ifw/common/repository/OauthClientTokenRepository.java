package com.isu.ifw.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.common.entity.OauthClientToken;

@Repository
public interface OauthClientTokenRepository extends JpaRepository<OauthClientToken, String> {


}
