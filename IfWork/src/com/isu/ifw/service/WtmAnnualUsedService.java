package com.isu.ifw.service;

import com.isu.ifw.vo.WtmAnnualCreateVo;
import org.springframework.stereotype.Service;

/**
 * 연차사용 Service
 */

public interface WtmAnnualUsedService {

	public void save(Long tenantId, String enterCd, String userId, String sabun, String yy, String taaTypeCd, String symd, String eymd, Double createCnt, String note) throws Exception;

	public WtmAnnualCreateVo getMyAnnualInfo(Long tenantId, String enterCd, String userId, String sabun, String yy, String symd, String eymd, String annualTaCd) throws Exception;

	public WtmAnnualCreateVo getMyCreatCnt(Long tenantId, String enterCd, String sabun, String yy, String annualTaCd) throws Exception;
}
