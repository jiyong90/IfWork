package com.isu.ifw.common.mapper;

import java.util.List;
import java.util.Map;

public interface CommUserMapper {
	public Map<String, Object> getCommUser(Map<String, Object> paramMap);
	public List<Map<String, Object>> getUserAuth(Map<String, Object> paramMap);
}
