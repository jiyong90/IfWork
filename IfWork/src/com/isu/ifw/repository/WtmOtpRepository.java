package com.isu.ifw.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isu.ifw.entity.WtmOtp;

@Repository
public interface WtmOtpRepository extends JpaRepository<WtmOtp, Long> {
	
	public List<WtmOtp> findByOtpAndResourceIdAndExpireDateGreaterThanEqualOrderByExpireDateDesc(String otp, Long resourceId, Date expireDate);
	
}
