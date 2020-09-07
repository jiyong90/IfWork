package com.isu.ifw.repository;

import com.isu.ifw.entity.WtmIfAppl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WtmIfApplRepository extends JpaRepository<WtmIfAppl, Long> {

}
