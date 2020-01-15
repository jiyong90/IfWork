package com.isu.ifw.mapper;

import java.util.Map;

public interface WtmEmpAddrMapper {
	
	public Map<String, Object> findByTenantIdAndEnterCdAndHandPhone(Map<String, Object> paramMap);
}
