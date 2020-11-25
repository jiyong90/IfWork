package com.isu.ifw.service;

import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.vo.WtmAnnualCreateVo;

import java.util.List;
import java.util.Map;

/**
 * 빗썸 연차기준관리
 */

public interface WtmAnnualCreateService {

	public List<WtmAnnualCreateVo> getList(Long tenantId, String enterCd, String sYmd, String searchKeyword, String searchType) throws Exception;

	public WtmAnnualCreateVo getByUserId(Long tenantId, String enterCd, String sYmd, String sabun, String taaTypeCd) throws Exception;

	public List<Map<String, Object>> getTaaTypeCd(Long tenantId, String enterCd) throws Exception;

	public Map<String, Object> convertWtmTaaCodetoMap(WtmTaaCode taaCode) throws Exception ;

	public void save(Long tenantId, String enterCd, String userId, String sabun, String yy, String taaTypeCd, String symd, String eymd, Integer createCnt, String note) throws Exception;

	public void save(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) throws Exception;

}
