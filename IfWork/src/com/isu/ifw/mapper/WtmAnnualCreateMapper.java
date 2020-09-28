package com.isu.ifw.mapper;


import com.isu.ifw.vo.WtmAnnualCreateVo;
import com.isu.ifw.vo.WtmCodeVo;

import java.util.List;
import java.util.Map;

public interface WtmAnnualCreateMapper {

	public List<WtmAnnualCreateVo> getAnnualCreateList(Map<String, Object> paramMap);

	List<WtmCodeVo> getAnnualCreateCodeList(Map<String, Object> paramMap);
}
