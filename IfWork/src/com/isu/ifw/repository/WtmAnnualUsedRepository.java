package com.isu.ifw.repository;

import com.isu.ifw.entity.WtmApplCode;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 연차사용내역 관리
 */
public interface WtmAnnualUsedRepository extends JpaRepository<WtmApplCode, Long> {
}
