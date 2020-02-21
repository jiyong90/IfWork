package com.isu.ifw.service;

import java.util.List;

import com.isu.ifw.vo.WtmApplLineVO;

/**
 * 
 * @author 
 *
 */
public interface WtmApplLineService {
	
	/**
	 * 신규 신청서 결재라인 조회
	 * @param tenantId
	 * @param enterCd
	 * @param sabun
	 * @param applCd
	 * @param userId
	 * @return
	 */
	public List<WtmApplLineVO> getApplLine(Long tenantId, String enterCd, String sabun, String applCd, String userId);
	
	/**
	 * 결재라인 저장
	 * @param tenantId
	 * @param enterCd
	 * @param apprLvl
	 * @param applId
	 * @param applCd
	 * @param sabun
	 * @param userId
	 */
	public void saveWtmApplLine(Long tenantId, String enterCd, int apprLvl, Long applId, String applCd, String sabun, String userId);
}
