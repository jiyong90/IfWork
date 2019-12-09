package com.isu.ifw.mapper;

import java.util.List;
import java.util.Map;

public interface WtmWorkteamMgrMapper {
	
	//근무조 코드 조회
	public List<Map<String, Object>> getWorkteamCdList(Map<String, Object> paramMap);
}
