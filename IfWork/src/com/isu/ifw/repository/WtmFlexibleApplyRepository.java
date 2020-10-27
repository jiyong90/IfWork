package com.isu.ifw.repository;

import com.isu.ifw.entity.WtmFlexibleApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WtmFlexibleApplyRepository extends JpaRepository<WtmFlexibleApply, Long> {}
