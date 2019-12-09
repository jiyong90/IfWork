package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmTimeCdMgrMapper {
	public int saveWtmTimeCdMgr(Map<String, Object> paramMap);
	public int saveWtmTimeBreakMgr(Map<String, Object> paramMap);
	
	public List<Map<String, Object>> getTimeCdMgrList(Map<String, Object> paramMap);
}
