package com.isu.ifw.mapper;


import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.vo.WtmAnnualCreateVo;
import com.isu.ifw.vo.WtmCodeVo;

import java.util.List;
import java.util.Map;

public interface WtmAnnualCreateMapper {

	public List<WtmAnnualCreateVo> getAnnualCreateList(Map<String, Object> paramMap);

	List<WtmCodeVo> getAnnualCreateCodeList(Map<String, Object> paramMap);

	WtmAnnualCreateVo getMyAnnualInfo(Map<String, Object> paramMap);

	List<WtmTaaCode> getTaaType(Map<String, Object> paramMap);

	WtmAnnualCreateVo getMyCreatCnt(WtmAnnualCreateVo annualCreateVo);

	List<WtmCodeVo> getAnnualUseCodeList(Map<String, Object> paramMap);

	WtmAnnualCreateVo getAnnualUsedInfo(Map<String, Object> paramMap);
}
