package com.isu.ifw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isu.ifw.entity.WtmWorktimeClose;

/**
 * 월마감
 */
public interface WtmWorktimeCloseRepository extends JpaRepository<WtmWorktimeClose, Long> {

}
